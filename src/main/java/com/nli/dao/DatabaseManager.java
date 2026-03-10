package com.nli.dao;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/vtu_project";
    private static final String USER = "javauser";
    private static final String PASS = "root";
    /* Gettind a connection to work:
    So, first we get the var conn as null so if it fails, it fails,
    First we get the Driver set up into the memory so java can run the .jar file for mariadb
    Then we try to get the conn using DriverManager.getConnection(URL,USER,PASS) to establish a connection
    between the SQL table and the java file using the private URL, USER and PASS
     */
    public Connection getConnection(){
        Connection conn = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL,USER,PASS);
        }catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }
    public void testMetadataExtraction(){
        try(Connection conn = getConnection()){
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Scanning for employees: \n");
            ResultSet rs = metaData.getColumns(null,null,"employees",null);
            while(rs.next()){
                String columnName=rs.getString("COLUMN_NAME");
                String columnType=rs.getString("TYPE_NAME");
                System.out.println("- " + columnName + " (" + columnType + ")");
            }
            System.out.println("-------------------------");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        DatabaseManager db = new DatabaseManager();
        db.testMetadataExtraction();
    }
}
