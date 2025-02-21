package fcaa.resources;

import org.testng.annotations.DataProvider;

public class SearchQueryDataProvider {
	@DataProvider(name = "booleanSearchQueries")
    public static Object[][] getSearchQueries() {
        return new Object[][]{
            // ----------------------AND/&&------------------------------
//             {"market AND finance"}, // pass for forms page
             {"market && finance"}, // pass for forms page
            // {"market &&finance"}, // test case fails  
            // ----------------------OR/||-------------------------------
//             {"market OR finance"}, // broken file link found at 341 for forms page
            // {"market finance"},  // broken file link found at 341 for forms page
             {"market || finance"},  // broken file link found at 341 for forms page
            // ----------------------NOT/-/!-------------------------------
             {"finance -market"}, // pass for forms page
//             {"market NOT finance"}, // broken file link found at 63 for forms page
//             {"finance !order"}, // pass for forms page
            // ----------------------Combination of AND/OR/NOT-------------------------------
             {"!finance -market AND orders"}, // pass for forms page
             {"management || order && contract"}, // broken file link found at 35 for forms page
            // {"management AND order OR contract"}, // broken file link found at 35 for forms page
//             {"market NOT finance !order"}, // broken file link found at 41 for forms page
//             {"market NOT finance orders"}, // broken file link found at 81 for forms page
//             {"management || order NOT contract"},  // broken file link found at 41 for forms page
             {"management AND order NOT contract"}, // pass for forms page
//             {"management && order && contract"}, // broken file link found at 35 for forms page
            // ----------------------Exact Operator-------------------------------
//             {"\"finance\""}, // pass for forms page
             {"\"finance transaction\""}, // pass for forms page
            // ----------------------Proximity Operator-------------------------------
//             {"maket~"}, // broken file link found at 141 for forms page
             {"fninancee~"}, // pass for forms page
            // ----------------------Proximity/?/* Operator-------------------------------
             {"finance*"}, // pass for forms page
             {"finan?ed"}, // pass for forms page
            // ----------------------Combination of AND/OR/NOT/()/Exact Operator-------------------------------
//             {"market NOT finance \"orders\""}, // broken file link found at 81 for forms page
             {"market AND (finance OR contract)"}, // broken file link found at 197 for forms page
//             {"!finance -market AND transaction"}, // pass for forms page
//             {"!(finance market) AND transaction"}, // pass for forms page
//             {"finance !order -market"}, // pass for forms page
//             {"\"finance transaction\" AND order"}, // pass for forms page
             {"market OR \"finance transaction\" -order"}, // broken file link found at 55 for forms page
//             {"(market finance) NOT abuse"}, // broken file link found at 327 for forms page
//             {"\"finance transaction\" -order"}, // pass for forms page
//             {"\"finance transaction\" market"}, // broken file link found at 152 for forms page
             {"(market AND finance) NOT contract"}, // pass for forms page
//             {"(market OR finance) NOT contract"}, // broken file link found at 163 for forms page
             {"(market OR finance) AND contract"}, // broken file link found at 169 for forms page
//             {"\"finance transaction\" NOT \"order\""}, // pass for forms page
//             {"market \"finance transaction\" NOT \"order\""},  // broken file link found at 55 for forms page
             {"finance && \"finance transaction\" NOT \"order\""}, // pass for forms page
//             {"(market OR finance) AND \"contract\""}, // broken file link found at 169 for forms page
            // ----------------------Combination of +/~/?/()/Exact/*/NOT Operator-------------------------------
//             {"finan* -mark?t"}, // broken file link found at 24 for forms page
             {"finance* AND marke*"}, // pass for forms page
//             {"financ* OR marke*"}, // broken file link found at 31 for forms page
//             {"(market AND finance) NOT contract*"}, // pass for forms page
             {"(market OR finance) NOT contract*"}, // pass for forms page
             {"(market AND finance) AND contract*"}, // pass for forms page
//             {"finance? && marke*"}, // pass for forms page
//             {"+finance +markets"}, // pass for forms page
//             {"-mark?t finan*"}, // broken file link found at 24 for forms page
//             {"-fninancee~ +mark?t"}, // broken file link found at 54 for forms page
//             {"fninan~ -mark?t +orders"}, // pass for forms page
//             {"fninan~ -mark?t -order"}, // pass for forms page
//             {"+ordre~ -orders"},// broken file link found at 12 for forms page
//             {"+ordre~ AND \"orders\""}, // should highlight orders text as well //pass
//             {"market +finance -order"}, // pass for forms page
//             {"market +finance"}, // pass for forms page
//             {"-finance +market"}, // broken file link found at 63 for forms page
             {"+finance -markt~"}, // test case fails for both glossary and form page
             {"fninance~ -order"}, // pass for forms page
            // ----------------------Some Other Combinations-------------------------------
//             {"fninan~ -mark?t OR ordre~"}, // broken file link found at 12 for forms page
//             {"fninan~ mark?t NOT ordre~"}, // test case fails for both glossary and form page
             {"-mark?t fninan~ AND ordree~"}, // pass for forms page
//             {"fninan~ -mark?t +ordree~"}, // broken file link found at 6 for forms page
//             {"fninan~ AND order"}, // pass for forms page
//             {"fninace~ OR mark?t -order"}, // broken file link found at 151 for forms page
//             {"fninan~ && ordr~"}, // pass for forms page
//             {"marke~ && finan?e !*ment"}, // pass for forms page
//             {"finance* AND \"market\""}, // pass for forms page
//             {"(marke* || finance*) AND (contract* OR order*)"}, // broken file link found at 201 for forms page
             {"(marke* || finance*) AND (\"transaction\" OR \"services\") NOT (contract* OR order*)"}, // broken file link found at 26 for forms page
             {"(mar* OR fin*) AND (\"review status\" OR \"documents\") NOT (leg* OR acc*)"}, // pass for forms page
//             {"fninance~ || order NOT manage"}, // broken file link found at 83 for forms page
//             {"market +finance || (order && management)"}, // pass for forms page
            // ------------------------------Proximity Fuzzy----------------------------------------
//             {"market \"finance abuse\"~5"}, // broken file link found at 145 for forms page
             {"(market \"finance transaction\"~5) -order"}, // broken file link found at 55 for forms page
//             {"order && \"finance transaction\"~3"}, // pass for forms page
             {"\"market order\"~5"}, // pass for forms page
//             {"\"market system\"~2"}, // pass for forms page
            // ------------------------------Boost Operator----------------------------------------
             {"market^2 finance"}, // broken file link found at 282 for forms page
//             {"market finance^2"}, // broken file link found at 218 for forms page
//             {"(market && abuse)^5 AND order"}, // pass for forms page
//             {"market finance^2 NOT transaction"}, // broken file link found at 342 for forms page
             {"(market && abuse)^5 (finance && abuse)^2 abuse"}, // pass for forms page
        };
    }
}
