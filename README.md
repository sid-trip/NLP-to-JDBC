# NLP to JDBC

A Java application that converts natural language queries into executable SQL statements and queries relational databases via JDBC.

## Index

1. [Overview](#overview)
2. [Features](#features)
3. [System Architecture](#system-architecture)
4. [Technology Stack](#technology-stack)
5. [Prerequisites](#prerequisites)
6. [Installation and Setup](#installation-and-setup)
7. [Usage Examples](#usage-examples)
8. [Contributing](#contributing)
9. [License](#license)

## Overview

NLP to JDBC is a Java web application designed to bridge the gap between human language and relational databases. Users can query connected databases using natural English language instead of writing manual SQL queries.

The application reads database schema metadata, maps input terms to schema entities and operators using semantic dictionaries and Levenshtein similarity, synthesizes an SQL query, executes it over JDBC, and renders the result.

## Features

- Dynamic schema discovery using JDBC metadata APIs.
- Natural language tokenization and phrase normalization.
- Typo tolerance using Levenshtein distance string similarity algorithms.
- Automatic translation of English comparisons to SQL operators.
- Web-based interface built with Servlets and JSP.

## System Architecture

The application processes requests through the following steps:

1. JSP Interface: User enters a natural language query.
2. SearchServlet: Routes the request to the processing components.
3. DatabaseManager: Loads table and column metadata from the database into memory.
4. SemanticParser: Sanitizes tokens, matches schema tables and columns, resolves operators, and synthesizes SQL.
5. JDBC Execution: Executes the synthesized SQL against the target database.
6. Result Rendering: Renders query results as an HTML table in the web interface.

## Technology Stack

- Language: Java 8 / Java 17
- Web Framework: Java Servlets, JSP
- Database Connectivity: JDBC, MySQL Connector / J
- Build Tool: Apache Maven

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven 3.6 or higher
- Apache Tomcat 9 or compatible Servlet container
- MySQL or MariaDB database server

## Installation and Setup

1. Clone the repository:
   git clone https://github.com/sid-trip/NLP-to-JDBC.git
   cd NLP-to-JDBC

2. Configure database credentials:
   Update database connection settings in DatabaseManager.java or project properties.

3. Build the project WAR file using Maven:
   mvn clean package

4. Deploy the generated WAR file (target/NLP-to-SQL-1.0-SNAPSHOT.war) to your Servlet container webapps directory.

## Usage Examples

Natural language query:
Show employees with salary greater than 50000

Generated SQL:
SELECT * FROM employees WHERE salary > 50000

Natural language query:
Find customers in New York

Generated SQL:
SELECT * FROM customers WHERE city = 'New York'

## Contributing

Contributions are welcome. Please follow these guidelines:

1. Fork the repository and create a new feature branch.
2. Ensure code changes follow standard Java coding conventions.
3. Keep pull requests focused and well documented.

Report issues or bug reports via the project issue tracker.

## License

This project is open source and available under the MIT License.
