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


}