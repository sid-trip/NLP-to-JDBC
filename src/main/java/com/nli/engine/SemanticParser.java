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
        //WASTE WORDS OR WORDS THAT HOLD NO VALUE
        stopWords.add("please");
        stopWords.add("um");
        stopWords.add("could");
        stopWords.add("you");
        stopWords.add("tell");
        stopWords.add("help");
        stopWords.add("out");
        stopWords.add("find");
        stopWords.add("me");
        stopWords.add("all");
        stopWords.add("is");
        stopWords.add("the");
    }
    /*
    This function is used to generate the SQL query.
    Logic: we get the string from the user, split and clean the input.
    if the cleaned input has words which are in our list of stop words, we dont do anything about them
    if one of the words is a column name in the given table, we add it to the colFound String.
    if one of the words is a word we used to map the operators, we get that operator using .containsKey()
    the last condition is to check whether the given word is a number as in the number of rows to be returned
     */
    public String parse(String input, List<String> dbcloumns){
        input = input.toLowerCase();
        input = input.replace("no more than", "lesser")
                .replace("not more than", "lesser")
                .replace("more than", "greater")
                .replace("greater than", "greater")
                .replace("less than", "lesser")
                .replace("equal to", "equal");
        input = input.replaceAll("[^a-zA-Z0-9 ]","");
        String[] words = input.split("\\s+");
        String table = "employees";
        String colFound = "";
        String opFound = "";
        String valFound = "";
        for (String word : words){
            if(stopWords.contains(word)){
                continue;
            }
            if(dbcloumns.contains(word)){
                colFound = word;
            }
            else if(operators.containsKey(word)){
                opFound = operators.get(word);
            }
            else if(word.matches("\\d+")){
                valFound = word;
            }
        }
        if (colFound.isEmpty() || opFound.isEmpty() || valFound.isEmpty()) {
            return "SELECT * FROM " + table;
        }
        return "SELECT * FROM " + table + " WHERE " + colFound + " " + opFound + " " + valFound;
    }

    public static void main(String[] args) {
        SemanticParser parser = new SemanticParser();

        List<String> mockColumns = Arrays.asList("emp_id", "name", "salary", "city", "department");

        String testInput = "Find me all the employees where the emp_id is more than 50";
        String resultSql = parser.parse(testInput, mockColumns);

        System.out.println("User Said: " + testInput);
        System.out.println("Generated SQL: " + resultSql);
    }
}
