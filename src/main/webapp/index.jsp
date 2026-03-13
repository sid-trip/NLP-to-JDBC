<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>NLP-to-SQL Engine</title>
    <style>
        body { font-family: 'JetBrains Mono', Tahoma, Geneva, Verdana, sans-serif; background-color: #f0f2f5; text-align: center; margin-top: 50px; }
        .container { background-color: white; width: 60%; margin: 0 auto; padding: 40px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        h1 { color: #333; }
        .search-box { width: 80%; padding: 15px; font-size: 18px; border: 2px solid #007bff; border-radius: 8px; margin-bottom: 20px; outline: none; }
        .btn { padding: 15px 30px; font-size: 18px; background-color: #007bff; color: white; border: none; border-radius: 8px; cursor: pointer; transition: 0.3s; }
        .btn:hover { background-color: #0056b3; }
        .result-card { margin-top: 30px; background-color: #282c34; color: #61afef; padding: 20px; border-radius: 8px; text-align: left; font-family: 'Courier New', Courier, monospace; font-size: 18px; }
        .label { color: #abb2bf; font-size: 14px; margin-bottom: 5px; }
    </style>
</head>
<body>

<div class="container">
    <h1>NLP-to-SQL Translator</h1>
    <p style="color: #666; margin-bottom: 30px;">Type a natural language query, and our engine will convert it into executable SQL.</p>

    <!-- The HTML Form: Action points to the @WebServlet("/search") -->
    <form action="search" method="POST">
        <label>
            <input type="text" name="userInput" class="search-box" placeholder="e.g., Find me the employees who live in the city Bangalore..." required>
        </label>
        <br>
        <button type="submit" class="btn">Generate SQL</button>
    </form>

    <!-- Dynamic Result Rendering (Only shows if Servlet sends data back) -->
    <% if(request.getAttribute("sqlResult") != null) { %>
    <div class="result-card">
        <div class="label">Natural Language Input:</div>
        <div style="color: #98c379; margin-bottom: 15px;">"<%= request.getAttribute("originalQuery") %>"</div>

        <div class="label">Generated SQL Statement:</div>
        <div><%= request.getAttribute("sqlResult") %></div>
    </div>
    <% } %>
</div>

</body>
</html>