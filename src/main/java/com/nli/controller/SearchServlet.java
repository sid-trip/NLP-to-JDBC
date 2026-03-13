package com.nli.controller;

import com.nli.dao.DatabaseManager;
import com.nli.engine.SemanticParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@WebServlet
public class SearchServlet extends HttpServlet{
    private SemanticParser parser;
    //Initializing the servlet, access the database exactly once, and getting the parser ready
    @Override
    public void init() throws ServletException{
        System.out.println("Starting up NLP-to-SQL engine.....");
        DatabaseManager db = new DatabaseManager();
        Map<String, List<String>> schema = db.getFullSchema();
        parser = new SemanticParser(schema);
        System.out.println("Engin ready!");
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String userQuery = request.getParameter("userInput");
        System.out.println("--- NEW REQUEST RECEIVED ---");//DEBUG
        System.out.println("User typed: " + userQuery);//DEBUG
        if(userQuery!=null && !userQuery.trim().isEmpty()){
            String generatedSql = parser.parse(userQuery);
            System.out.println("Engine generated: "+generatedSql);//DEBUG
            request.setAttribute("originalQuery",userQuery);
            request.setAttribute("sqlQuery",generatedSql);
        }else{
            System.out.println("Returned USER query was empty");
        }
        System.out.println("Forwarding to index.jsp...");//DEBUG
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }

}