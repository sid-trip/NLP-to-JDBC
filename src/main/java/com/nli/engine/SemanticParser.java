package com.nli.engine;
import java.awt.datatransfer.FlavorEvent;
import java.sql.Array;
import java.util.*;
public class SemanticParser {
    final private Map<String, String> operators = new HashMap<>();
    private List<String> stopWords = new ArrayList<>();
    private Map<String, List<String>> SchemaMap = new HashMap<>();

    public SemanticParser(Map<String, List<String>> databaseSchema) {
        this.SchemaMap = databaseSchema;
        //ALL OPERATIONS THAT WOULD BE DETERMINING GREATER OR LESSER THAN THE GIVEN
        operators.put("above", ">");
        operators.put("below", "<");
        operators.put("more", ">");
        operators.put("less", "<");
        operators.put("greater", ">");
        operators.put("lesser", "<");
        operators.put("higher", ">");
        operators.put("lower", "<");
        //WASTE WORDS OR WORDS THAT HOLD NO VALUE
        stopWords.addAll(Arrays.asList("please", "find", "me", "show", "all", "the", "a", "an", "where", "whose", "having", "with", "than", "live", "located"));
//        stopWords.add("please");
//        stopWords.add("um");
//        stopWords.add("could");
//        stopWords.add("you");
//        stopWords.add("tell");
//        stopWords.add("help");
//        stopWords.add("out");
//        stopWords.add("find");
//        stopWords.add("me");
//        stopWords.add("all");
//        stopWords.add("is");
//        stopWords.add("the");
//        stopWords.addAll(Arrays.asList("all", "the", "is", "a", "an", "than", "where"));
//
    }

    /*
    Change: changing from hardcoded to dynamically getting the DB table name and the searching for the columns names,
    checking the operators present, and if the sentence contains any numbers
    */
    public String parse(String input, List<String> dbcloumns) {
        input = input.toLowerCase().replaceAll("[^a-zA-Z0-9_ ]", "");
        String[] words = input.split("\\s+");
        String tableFound = "";
        String colFound = "";
        String opFound = "";
        String valFound = "";
        for (String word : words) {
            if (stopWords.contains(word)) continue;
            //Finding the tableName and assigning it to tableFound
            for (String tableName : SchemaMap.keySet()) {
                if (levenshtein_similarity(word, tableName) > 0.8) {//We use levenshtein to make it fuzzy search, typos are allowed
                    tableFound = tableName;
                    break;
                }
            }
            //Finding the columnName IN the tableName
            for (Map.Entry<String, List<String>> entry : SchemaMap.entrySet()) {//checking the entry in the SchemaMap
                for (String colName : entry.getValue()) {
                    if (levenshtein_similarity(colName, word) > 0.8) {
                        colFound = colName;
                        //This is if we haven't found the table but got the column name we just get it from the entry map
                        if (tableFound.isEmpty()) tableFound = entry.getKey();
                        break;
                    }
                }
            }
            //finding operators
            if (operators.containsKey(word)) {
                opFound = operators.get(word);
            }
            //finding values
            else if (word.matches("\\d+")) {
                valFound = word;
            } else if (!tableFound.isEmpty() && !colFound.isEmpty() && !word.equals(tableFound) && !word.equals(colFound)) {
                valFound = "'" + recoverCase(word, originalInput) + "'";
            }
        }
        if (tableFound.isEmpty() && !SchemaMap.isEmpty()) {
            tableFound = SchemaMap.keySet().iterator().next();
        }
        //return logic:
        if (colFound.isEmpty() || opFound.isEmpty() || valFound.isEmpty()) {
            return "SELECT * FROM " + tableFound;
        }
        return "SELECT * FROM " + tableFound + " WHERE " + colFound + " " + opFound + " " + valFound;
    }
    public float getSimilarity(String s1, String s2) {
        int i =0;
        float count =0;
        char[] c2 = s2.toCharArray();
        char[] c1 = s1.toCharArray();
        char[] big, small;
        if (c1.length>=c2.length){
            big = c1;
            small = c2;
        }else{
            small = c1;
            big = c2;
        }
        for (i = 0; i<small.length; i++){
            if (c1[i]==c2[i]){
                count++;
            }
        }
        return count/big.length;
    }
    public float levenshtein_similarity(String s1, String s2){
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n+1][m+1];
        for(int i = 0;i<n;i++){
            for(int j = 0;j<m;j++){
                if(i==0){
                    dp[i][j]=j;
                }
                else if(j==0){
                    dp[i][j]=i;
                }else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
                int distance = dp[n][m];
            }
        }
        int distance = dp[n][m];
        return 1.0f - (float)distance/Math.max(n,m);
    }

    public static void main(String[] args) {
        SemanticParser parser = new SemanticParser();

        List<String> mockColumns = Arrays.asList("emp_id", "name", "salary", "city", "department");

        String testInput = "find me the employees whose name who live in the city Bangalore";
        String resultSql = parser.parse(testInput, mockColumns);

        System.out.println("User Said: " + testInput);
        System.out.println("Generated SQL: " + resultSql);
    }
}
