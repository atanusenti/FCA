package fcaa.resources;

import org.testng.annotations.DataProvider;

public class SearchQueryDataProvider {
	@DataProvider(name = "booleanSearchQueries")
    public static Object[][] getSearchQueries() {
        return new Object[][]{
            // ----------------------AND/&&------------------------------
//            {"market AND finance"},
            // {"market && finance"}, 
            // {"market &&finance"}, // test case fails  
            
            // ----------------------OR/||-------------------------------
            // {"market OR finance"}, // test case fails at 341
            // {"market finance"},
            {"market || finance"},   
            
            // ----------------------NOT/-/!-------------------------------
            {"finance -market"},
            // {"market NOT finance"}, 
            // {"finance !order"},
            
            // ----------------------Combination of AND/OR/NOT-------------------------------
            // {"!finance -market AND orders"},
            {"management || order && contract"}, // test case fails at 35
            // {"management AND order OR contract"}, // test case fails at 35
            // {"market NOT finance !order"},
            // {"market NOT finance orders"},
            // {"management || order NOT contract"},
            // {"management AND order NOT contract"},
            // {"management && order && contract"},
            
            // ----------------------Exact Operator-------------------------------
            // {"\"finance\""},
            {"\"finance transaction\""},
            
            // ----------------------Proximity Operator-------------------------------
            // {"maket~"},
            // {"fninancee~"},
            
            // ----------------------Proximity/?/* Operator-------------------------------
            // {"finance*"},
            // {"finan?e"},
            
            // ----------------------Combination of AND/OR/NOT/()/Exact Operator-------------------------------
            {"market NOT finance \"orders\""},
            // {"market AND (finance OR contract)"},
            // {"!finance -market AND transaction"},
            // {"!(finance market) AND transaction"},
            // {"finance !order -market"},
            // {"\"finance transaction\" AND order"},
            // {"market OR (finance !manage)"}, // test case fails
            // {"market OR \"finance transaction\" -order"},
            // {"(market finance) NOT abuse"},
            // {"\"finance transaction\" -order"},
            // {"\"finance transaction\" market"},
            // {"(market AND finance) NOT contract"},
            // {"(market OR finance) NOT contract"},
            // {"(market OR finance) AND contract"},
            // {"\"finance transaction\" NOT \"order\""},
            // {"market \"finance transaction\" NOT \"order\""},
            {"finance && \"finance transaction\" NOT \"order\""},
            // {"(market OR finance) AND \"contract\""},
            
            // ----------------------Combination of +/~/?/()/Exact/*/NOT Operator-------------------------------
            // {"finan* -mark?t"},
            // {"finance* AND marke*"},
            // {"financ* OR marke*"},
            {"(market AND finance) NOT contract*"},
            // {"(market OR finance) NOT contract*"},
            // {"(market AND finance) AND contract*"},
            // {"financ? && marke*"},
            // {"+finance +market"},
            // {"-mark?t finan*"},
            // {"-fninancee~ +mark?t"},
            // {"fninan~ -mark?t +orders"},
            // {"fninan~ -mark?t -order"},
            // {"+ordre~ -orders"},
            // {"+ordre~ AND \"orders\""}, // should highlight orders text as well
            // {"market +finance -order"},
            // {"market +finance"},
            // {"-finance +market"},
            // {"+finance -markt~"}, // test case fails
            // {"fninance~ -order"},
            
            // ----------------------Some Other Combinations-------------------------------
            // {"fninan~ -mark?t OR ordre~"},
            // {"fninan~ mark?t NOT ordre~"},// test case fails
            // {"-mark?t fninan~ AND ordree~"},
            // {"fninan~ -mark?t +ordree~"},
            // {"fninan~ AND order"},
            // {"fninace~ OR mark?t -order"},
            // {"fninan~ && ordr~"},
            // {"marke~ && finan?e !*ment"},
            // {"finance* AND \"market\""},
            // {"(marke* || finance*) AND (contract* OR order*)"},
            {"(marke* || finance*) AND (\"transaction\" OR \"services\") NOT (contract* OR order*)"},
            {"(mar* OR fin*) AND (\"review status\" OR \"documents\") NOT (leg* OR acc*)"},
            // {"fninance~ || order NOT manage"},
            // {"market +finance || (order && management)"},
            
            // ------------------------------Proximity Fuzzy----------------------------------------
            {"market \"finance abuse\"~5"},
            // {"(market \"finance transaction\"~5) -order"},
            // {"order && \"finance transaction\"~3"},
            {"\"market order\"~5"},
            // {"\"market system\"~2"},
            
            // ------------------------------Boost Operator----------------------------------------
            {"market^2 finance"},
            // {"market finance^2"},
            // {"(market && abuse)^5 AND order"},
            // {"market finance^2 NOT transaction"},
            {"(market && abuse)^5 (finance && abuse)^2 abuse"},
        };
    }
}
