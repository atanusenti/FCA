package fcaa.AbstractComponnent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.Reporter;

public class BooleanSearch {
	static int dataPosition = 0;
	public boolean evaluateQuery(String query, String text, final int actualPosition) {
		dataPosition = actualPosition;
		query = query.trim();

		// Handle boosted terms (^)
		if (query.contains("^")) {
			return evaluateBoostedTerms(query, text, dataPosition);
		}

		// Handle parentheses (highest precedence)
		query = handleParentheses(query, text);

		// Handle individual terms or default matches
		if (query.equals("1"))
			return true; // Boolean true
		if (query.equals("0"))
			return false; // Boolean false

		// Handle logical AND (&&)
		if (query.contains(" AND ") || query.contains("&&")) {
			return evaluateLogicalOperator(query, text, "AND|&&");
		}

		// Handle logical OR (|| or implicit OR with spaces)
		if ((query.contains(" OR ") || query.contains("||") || query.matches(".*\\s+.*")
				|| query.matches(".*[~?]\\s.*"))
				&& !(query.matches("^\"[^\"]+\"$") || query.matches("^\"[^\"]+\"\\s*$")
						|| query.matches("^\"[^\"]+\"~\\d+$"))
				&& !query.contains("NOT ")) {
			return evaluateLogicalOperator(query, text, "OR|\\|\\|");
		}

		// Handle logical NOT (-, !)
		if (query.contains("NOT ") || query.contains("-") || query.contains("!")) {
			return evaluateLogicalNot(query, text, dataPosition);
		}

		// Handle proximity terms (~)
		if (query.contains("~")) {
			return evaluateProximityOrFuzzy(query, text);
		}

		// Handle wildcard or regex quantifiers (*, ?)
		if (query.contains("*") || query.contains("?")) {
			return evaluateTerm(query, text);
		}

		// Handle exact phrase match ("" - double quotes)
		if (query.startsWith("\"") && query.endsWith("\"")) {
			return evaluateTerm(query, text);
		}

		// Handle mandatory terms (+)
		if (query.contains("+")) {
			return evaluateMandatoryTerms(query, text, dataPosition);
		}

		// Default: evaluate term or individual term matching
		return evaluateTerm(query, text);
	}

	private String handleParentheses(String query, String text) {
		while (query.contains("(")) {
			int openIndex = query.lastIndexOf("(");
			int closeIndex = query.indexOf(")", openIndex);
			if (closeIndex == -1) {
				throw new IllegalArgumentException("Unmatched parentheses in query.");
			}

			// Evaluate the nested expression
			String nestedQuery = query.substring(openIndex + 1, closeIndex).trim();
			boolean nestedResult = evaluateQuery(nestedQuery, text, dataPosition);
			query = query.substring(0, openIndex) + (nestedResult ? "1" : "0") + query.substring(closeIndex + 1);
		}
		return query;
	}

	private boolean evaluateLogicalNot(String query, String text, int dataPosition) {
		String[] parts;

		if (query.contains("NOT")) {
			List<String> partList = new ArrayList<>();
			// Regex to correctly extract terms while preserving "NOT term"
			String regex = "\"[^\"]+\"|\\bNOT\\s+\"[^\"]+\"|\\bNOT\\s+\\S+|\\S+";
	        Matcher matcher = Pattern.compile(regex).matcher(query);

			while (matcher.find()) {
				String match = matcher.group().trim();
				if (!match.isEmpty()) {
					partList.add(match);
				}
			}

			parts = partList.toArray(new String[0]);
		} else {
			parts = query.split("\\s+");
		}

		boolean result = false;
		for (String part : parts) {
			part = part.trim();

			if (part.isEmpty() || part.equals("\"\"")) {
				continue;
			}

			// Skip already resolved boolean literals
			if (part.equals("1")) {
				continue;
			}
			if (part.equals("0")) {
				return false; // If literal "0" is encountered, treat as false immediately
			}

			// Handle negation terms (NOT, -, !)
			if (part.startsWith("NOT ")) {
				String term = part.substring(4).trim(); // Remove "NOT "
				if (evaluateQuery(term, text, dataPosition)) {
					System.out.println(text);
					Reporter.log("Error in Data present at : " + (dataPosition), true);
					Assert.fail("Negation term is found in the search result");
					return false; // Negation fails if the term is present
				} else {
					result = true;
				}
			} else if (part.startsWith("-") || part.startsWith("!")) {
				String term = part.replaceFirst("^[-!]+", "").trim();
				if (evaluateQuery(term, text, dataPosition)) {
					System.out.println(text);
					Reporter.log("Error in Data present at : " + (dataPosition), true);
					Assert.fail("Negation term is found in the search result");
					return false; // Negation fails if the term is present
				} else {
					result = true;
				}
			} else {
				boolean isPositiveTermFound = evaluateQuery(part, text, dataPosition);
				result = result || isPositiveTermFound;
			}
		}
		return result; // Return true if any positive term is present
	}

	private boolean evaluateLogicalOperator(String query, String text, String operatorRegex) {
		// Check for AND/OR operators explicitly
		boolean hasExplicitLogicalOperators = query.contains(" AND ") || query.contains("&&") || query.contains(" OR ")
				|| query.contains("||");

		String[] parts;

		if (query.contains("^") && ((query.matches(".*\\)\\^\\d+\\s+.*") || query.contains("&& ("))
				|| (query.matches(".*\\)\\^\\d+\\s+AND\\s+.*") || query.contains("AND (")))) {
			String regex = "\\s+(?i)(AND|&&)\\s+(?![^()]*\\))";
			// Split the query while keeping expressions inside parentheses together
			parts = query.split(regex);
		} else if (operatorRegex.contains("AND")) {
			parts = query.split("(?i)\\s+AND\\s+|\\s*&&\\s*"); // Split on AND or &&
		} else if (operatorRegex.contains("OR")) {
			if (!hasExplicitLogicalOperators) {
				// Split by space but exclude exact operator
				parts = query.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)\\s+");
			} else {
				// If explicit logical operators are used, only split on OR/|| operators
				parts = query.split("(?i)\\s+OR\\s+|\\s*\\|\\|\\s*");
			}
		} else {
			throw new IllegalArgumentException("Invalid operator regex: " + operatorRegex);
		}

		if (operatorRegex.contains("AND")) {
			for (String part : parts) {
				if (!evaluateQuery(part.trim(), text, dataPosition)) {
					return false; // If any part is false, the whole AND condition is false
				}
			}
			return true; // All parts are true
		} else if (operatorRegex.contains("OR")) {
			boolean result = false;
			for (String part : parts) {
				boolean evaluationResult = evaluateQuery(part.trim(), text, dataPosition);
				result = result || evaluationResult;
			}
			return result;
		}
		return false;
	}

	private boolean evaluateBoostedTerms(String query, String text, int dataPosition) {
		List<String> parts = new ArrayList<>();
		List<String> unboostedTerms = new ArrayList<>();

		boolean isSpaceOutsideParenthesis = query.matches(".*\\)\\^\\d+\\s+.*") || query.matches(".*\\s+\\(.*");

		if (query.contains("(")
				&& (query.matches(".*\\)\\s*(AND|&&)\\s+.*") || query.matches(".*\\s+(AND|&&)\\s*\\(.*"))
				&& isSpaceOutsideParenthesis) {
			return evaluateLogicalOperator(query, text, "AND|&&");
		}

//		if(query.contains("(") && (query.matches(".*\\)\\s*(OR|\\|\\|)\\s+.*") || query.matches(".*\\s+(OR|\\|\\|)\\s*\\(.*")) && isSpaceOutsideParenthesis) {
//		   return evaluateLogicalOperator(query, text, "OR|\\|\\|");
//		}

		if (query.contains(" NOT ")) {
			return evaluateLogicalNot(query, text, dataPosition);
		}

		// Regular expression to split by space or AND/&& outside of parentheses
		if (query.contains("(")) {
			String regex = "(?<=\\)\\^\\d+)\\s+|\\s+(?=\\()";
			parts = new ArrayList<>(List.of(query.split(regex)));
		} else {
			// Simple split by space if no parentheses are found
			parts = new ArrayList<>(List.of(query.split("\\s+")));
		}
		parts.removeIf(String::isEmpty);

		int maxBoostScore = 0; // Keep track of the highest boost score
		boolean hasUnboostedMatch = false; // If any unboosted term is found

		for (String part : parts) {
			int boostScore = 1;
			String baseTerm = part;

			if (part.contains("^")) {
				String[] parts2 = part.split("\\^");
				baseTerm = parts2[0].trim();
				boostScore = Integer.parseInt(parts2[1]);
			} else {
				unboostedTerms.add(part.trim()); // Store unboosted term for later check
			}

			boolean isMatch = false;
			// Handle grouped terms `(market && abuse)^5`
			if (baseTerm.startsWith("(") && baseTerm.endsWith(")")) {
				baseTerm = baseTerm.substring(1, baseTerm.length() - 1);
				String[] subTerms = baseTerm.split("\\s*&&\\s*");

				boolean allSubTermsMatch = true;
				for (String subTerm : subTerms) {
					if (!evaluateQuery(subTerm.trim(), text, dataPosition)) {
						allSubTermsMatch = false;
						break;
					}
				}
				isMatch = allSubTermsMatch;
			} else {
				isMatch = evaluateQuery(baseTerm, text, dataPosition);
			}
			if (isMatch) {
				maxBoostScore = Math.max(maxBoostScore, boostScore);
			}
		}

		// Check if any unboosted term is present in text
		for (String term : unboostedTerms) {
			if (evaluateQuery(term, text, dataPosition)) {
				hasUnboostedMatch = true;
				break;
			}
		}

		return hasUnboostedMatch || maxBoostScore > 0;
	}

	private boolean evaluateMandatoryTerms(String query, String text, int dataPosition) {
		String[] terms = query.split("\\s+");
		for (String term : terms) {
			if (term.startsWith("+")) {
				if (!evaluateQuery(term.substring(1).trim(), text, dataPosition)) {
					Reporter.log("Error in Data present at : " + (dataPosition), true);
					Assert.fail("Mandatory term not found in the search result");
					return false; // Mandatory term not found
				}
			}
		}
		return true;
	}

	private boolean evaluateTerm(String term, String text) {
		term = term.trim();

		// Handle exact phrases (enclosed in double quotes)
		if (term.startsWith("\"") && term.endsWith("\"")) {
			String exactPhrase = term.substring(1, term.length() - 1).trim(); // Remove quotes only from start and end
		    // Check for exact word match using regex to avoid substring issues
		    return Pattern.compile("\\b" + Pattern.quote(exactPhrase) + "\\b").matcher(text).find();
		}

		// Handle wildcards (* and ?)
		if (term.contains("*") || term.contains("?")) {
			String regex = "\\b" + term.replace("*", ".*").replace("?", ".") + "\\b";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			return pattern.matcher(text).find();
		}

//		return text.contains(term);
		return containsTerm(term, text);
	}

	private boolean containsTerm(String term, String text) {
		String regex = "\\b" + Pattern.quote(term.trim().toLowerCase()) + "\\b";
		return Pattern.compile(regex).matcher(text).find();
	}

	// Evaluate proximity or fuzzy matching (~)
	private boolean evaluateProximityOrFuzzy(String query, String text) {

		// Handle proximity terms (~) as well as OR
		if (query.contains("~") && (query.contains(" OR ") || query.contains("||"))) {
			return evaluateLogicalOperator(query, text, "OR|\\|\\|");
		}

		if (query.matches(".*[~?]\\s.*") && !(query.matches("^\"[^\"]+\"$") || query.matches("^\"[^\"]+\"\\s*$"))) {
			return evaluateLogicalOperator(query, text, "OR|\\|\\|");
		}

		// Existing proximity handling
		if (query.matches(".*\".*\"~\\d+.*")) {
			return evaluateProximityQuery(query, text); // Handle proximity query
		}

		if (query.startsWith("+") && query.endsWith("~")) {
			return evaluateMandatoryTerms(query, text, dataPosition);
		}

		if ((query.startsWith("*") && query.endsWith("~")) || ((query.startsWith("?") && query.endsWith("~")))) {
			// Extract actual term by removing '*' from start and '~' from end
			String processedQuery = query.substring(1, query.length() - 1); // Remove * and ~

			// Convert wildcard (*) to regex format
			String regex = "(?i)\\b\\w*" + processedQuery.replace("*", ".*").replace("?", ".") + "\\w*\\b";

			// Compile regex and find matches in the text
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);

			List<String> candidateWords = new ArrayList<>();

			while (matcher.find()) {
				candidateWords.add(matcher.group()); // Store words matching wildcard pattern
			}

			// If no candidate words match, return false
			if (candidateWords.isEmpty()) {
				return false;
			}

			boolean result = true;
			// Check for approximate match (~) on identified candidate words
			for (String candidate : candidateWords) {
				boolean isFound = containsApproximateMatch(candidate, text);
				result = result && isFound;
			}

			return result; // No approximate match found
		}

		// Fuzzy match: validate term structure
		String[] terms = query.split("~");
		String term = terms[0].trim();
		return containsApproximateMatch(term, text);
	}

	// Proximity matching logic
	private static boolean evaluateProximityQuery(String query, String text) {
		// Extract words and proximity from query
		Pattern pattern = Pattern.compile("\"(.*?)\"~?(\\d*)");
		Matcher matcher = pattern.matcher(query);

		if (!matcher.find()) {
			System.out.println("Error: Query format is incorrect.");
			return false;
		}

		String[] phraseWords = matcher.group(1).split("\\s+");
		int proximity = matcher.group(2).isEmpty() ? 1 : Integer.parseInt(matcher.group(2));

		if (phraseWords.length != 2) {
			System.out.println("Error: Query should contain exactly two words.");
			return false;
		}

		String word1 = phraseWords[0].toLowerCase();
		String word2 = phraseWords[1].toLowerCase();

		// Normalize text
		text = text.replaceAll("[’']", "").replaceAll("-", " ").replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

		String[] words = text.split("\\s+"); // Ensure correct word splitting

		List<Integer> word1Indexes = new ArrayList<>();
		List<Integer> word2Indexes = new ArrayList<>();

		// Step 1: Store all positions of both words
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals(word1)) {
				word1Indexes.add(i);
			}
			if (words[i].equals(word2)) {
				word2Indexes.add(i);
			}
		}

		// Step 2: Check if any pair of (word1, word2) is within proximity range
		for (int i : word1Indexes) {
			for (int j : word2Indexes) {
				if (Math.abs(i - j) - 1 <= proximity) { // Ensure words are within range
					return true;
				}
			}
		}

		return false;
	}

	// Handles wildcard (~) fuzzy matching
	private boolean containsApproximateMatch(String term, String text) {
		// Sanitize the input term and text, ensuring only alphanumeric characters are
		// included
		String sanitizedTerm = term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(); // Remove non-alphanumeric,lower case
		String[] words = text.split("\\s+");

		for (String word : words) {
			String sanitizedWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

			if (sanitizedWord.isEmpty()) {
				continue;
			}

			if (calculateLevenshteinDistance(sanitizedTerm, sanitizedWord) <= 3) {
				return true;
			}
		}

		return false; // Return false if no match is found
	}

	// Levenshtein distance algorithm for approximate matching
	private int calculateLevenshteinDistance(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			for (int j = 0; j <= len2; j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
				}
			}
		}
		return dp[len1][len2];
	}
}
