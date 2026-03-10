package com.nli.engine;
import java.util.*;
public class SemanticParser {
    private Map<String, String> operators = new HashMap<>();
    private List<String> stopWords = new ArrayList<>();
    public SemanticParser(){
        //ALL OPERATIONS THAT WOULD BE DETERMINING GREATER OR LESSER THAN THE GIVEN
        operators.put("above",">");
        operators.put("below","<");
        operators.put("more",">");
        operators.put("less","<");
        operators.put("greater",">");
        operators.put("lesser","<");
        operators.put("higher",">");
        operators.put("lower","<");
        operators.put("",">");
        //WASTE WORDS OR WORDS THAT HOLD NO VALUE
        stopWords.add("please");
        stopWords.add("um");
        stopWords.add("could");
        stopWords.add("you");
        stopWords.add("tell");
        stopWords.add("help");
        stopWords.add("out");

    }
}
