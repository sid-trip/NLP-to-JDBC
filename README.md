# NLP-to-JDBC

> **A Human-to-SQL translation engine** — query any JDBC-connected database using plain English, no SQL knowledge required.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Open Source](https://img.shields.io/badge/Open%20Source-%E2%9D%A4-brightgreen)](https://github.com/sid-trip/NLP-to-JDBC)
[![Contributions Welcome](https://img.shields.io/badge/Contributions-Welcome-orange.svg)](#contributing)

---

## Table of Contents

- [Overview](#overview)
- [Core Idea](#core-idea)
- [How It Works — The 4-Step Process](#how-it-works--the-4-step-process)
- [Project Architecture & Workflow](#project-architecture--workflow)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Example Query Translation](#example-query-translation)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

**NLP-to-JDBC** is an open-source Java web application that bridges the gap between human language and relational databases. It empowers non-technical users — analysts, managers, domain experts — to retrieve data by typing natural language questions instead of writing SQL.

Under the hood, the engine tokenises the user's input, maps words and phrases to SQL constructs via a semantic dictionary, synthesises a valid SQL query, executes it over JDBC, and returns the results as a formatted HTML table — all in a single request/response cycle.

---

## Core Idea

Databases hold enormous value, but that value is locked behind SQL — a language most business users never learn. **NLP-to-JDBC** acts as a *Simplified Search Engine for a Database that writes its own SQL queries on the fly*:

```
"Show employees with salary above 50000"
            ↓
    SELECT * FROM employees WHERE salary > 50000
```

No SQL. No code. Just plain English.

---

## How It Works — The 4-Step Process

### 1 · Schema Intelligence

When the application starts it uses **JDBC Metadata APIs** to inspect the connected database automatically. It learns every table name and every column name without any manual configuration. This live schema awareness means the engine always works against the real structure of the database. Add a table or column, and the engine picks it up on the next start.

### 2 · Semantic Mapping

A **synonym dictionary** built with Java `HashMap`s maps natural-language words and phrases to SQL keywords and operators:

| Natural Language Phrase | SQL Equivalent |
|---|---|
| `show` / `get` / `list` / `find` / `display` / `fetch` | `SELECT *` |
| `higher than` / `above` / `more than` / `greater than` | `>` |
| `lower than` / `below` / `less than` | `<` |
| `equal to` / `is` / `equals` | `=` |
| `not` / `excluding` / `except` | `!=` |
| `and` / `also` | `AND` |
| `or` | `OR` |

This dictionary is the single source of truth for the mapping layer and is the primary place contributors can expand language coverage.

### 3 · The NLP Parser

When a query arrives, the **NLP Engine** tokenises the input string and applies the semantic map to each token:

| Input Token | Resolved Element | SQL Fragment |
|---|---|---|
| `Show` | Action → `SELECT *` | `SELECT *` |
| `employees` | Schema table match | `FROM employees` |
| `salary above 50000` | Column + operator + value | `WHERE salary > 50000` |

The parser identifies three structural components — *action*, *target*, and *condition* — and hands them to the SQL Synthesiser.

### 4 · Dynamic SQL Execution

The synthesised SQL string is passed to **JDBC**, which executes it against the database. The `ResultSet` is converted into an HTML table by `HTMLBuilder` and streamed back to the browser via the Servlet → JSP pipeline.

---

## Project Architecture & Workflow

The full request/response lifecycle is:

```
JSP (user types query)
       │
       ▼
Servlet — Controller
       │
       ▼
NLP Engine — Tokenise & Map tokens to SQL constructs
       │
       ▼
SQL Synthesis — Assemble final SQL string
       │
       ▼
JDBC — Execute query against the database
       │
       ▼
HTMLBuilder — Java converts ResultSet → HTML table string
       │
       ▼
Servlet — Attaches HTML string to response
       │
       ▼
JSP — Renders output to the user
```

### Component Responsibilities

| Component | Role |
|---|---|
| **JSP (Input)** | Presents the search form; captures the natural-language query string |
| **Servlet** | Acts as the MVC Controller; routes the request and response |
| **NLP Engine** | Tokenises the query; performs synonym resolution via `HashMap` |
| **SQL Synthesiser** | Concatenates resolved tokens into a valid, executable SQL statement |
| **JDBC Layer** | Opens a connection; executes the SQL; returns a `ResultSet` |
| **HTMLBuilder** | Iterates over the `ResultSet` and builds an HTML `<table>` string |
| **JSP (Output)** | Renders the HTML table returned by the Servlet |

---

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | JSP (JavaServer Pages) |
| Controller | Java Servlet (MVC) |
| NLP / Parsing | Plain Java — `String`, `HashMap`, tokenisation utilities |
| Database Connectivity | JDBC (Java Database Connectivity) |
| Schema Discovery | JDBC `DatabaseMetaData` |
| Output Rendering | Java `HTMLBuilder` utility + JSP |
| Build Tool | Maven / standard Java project structure |

---

## Getting Started

### Prerequisites

- Java 11 or higher (Java 17 LTS recommended)
- A Java Servlet container (Apache Tomcat 9+ recommended)
- A JDBC-compatible relational database (MySQL, PostgreSQL, H2, etc.)
- Maven (optional, for dependency management)

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/sid-trip/NLP-to-JDBC.git
   cd NLP-to-JDBC
   ```

2. **Configure the database connection**

   Update the JDBC URL, username, and password in `src/main/resources/db.properties`:

   ```properties
   db.url=jdbc:mysql://localhost:3306/your_database
   db.user=your_username
   db.password=your_password
   ```

3. **Build and deploy**

   ```bash
   mvn clean package
   # Copy the generated WAR to your Tomcat webapps directory
   cp target/nlp-to-jdbc.war $CATALINA_HOME/webapps/
   ```

4. **Open the application**

   Navigate to `http://localhost:8080/nlp-to-jdbc` in your browser, type a natural-language query, and press **Search**.

---

## Example Query Translation

| Natural Language Query | Generated SQL |
|---|---|
| `Show all employees` | `SELECT * FROM employees` |
| `List employees with salary above 50000` | `SELECT * FROM employees WHERE salary > 50000` |
| `Find customers in New York` | `SELECT * FROM customers WHERE city = 'New York'` |
| `Get orders with amount less than 200` | `SELECT * FROM orders WHERE amount < 200` |
| `Display products where stock is 0` | `SELECT * FROM products WHERE stock = 0` |

---

## Contributing

**NLP-to-JDBC is fully open source and welcomes contributions of all sizes.** Whether you are fixing a typo, adding a synonym, improving the parser, or implementing a completely new feature, your help is appreciated.

### Ways to Contribute

| Contribution Type | Description |
|---|---|
| 🗺️ **Synonym Expansion** | Add new natural-language synonyms to the semantic dictionary |
| 🛠️ **Parser Improvements** | Improve token resolution logic, handle edge cases, support `JOIN` / `GROUP BY` |
| 🐛 **Bug Fixes** | Report and fix unexpected query translations or runtime errors |
| 🧪 **Tests** | Write unit tests for the NLP engine or integration tests for the JDBC layer |
| 📖 **Documentation** | Improve the README, add Javadoc, or write usage guides |
| 🎨 **UI / UX** | Improve the JSP front-end layout and user experience |

### Contribution Workflow

1. **Fork** this repository and create a new branch:

   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** — keep commits small and focused.

3. **Test** your changes locally against a real database.

4. **Open a Pull Request** against the `main` branch with a clear description of what you changed and why.

5. A maintainer will review your PR, suggest any changes, and merge it.

### Code Style Guidelines

- Follow standard Java naming conventions (camelCase for variables/methods, PascalCase for classes).
- Keep methods short and single-purpose.
- Add a brief comment above any non-obvious logic, especially in the NLP parser and SQL synthesiser.
- Avoid breaking existing public method signatures without a deprecation notice.

### Reporting Issues

Please open a [GitHub Issue](https://github.com/sid-trip/NLP-to-JDBC/issues) with:
- A clear, descriptive title.
- The natural-language query you entered.
- The SQL that was generated (if any).
- The expected SQL output.
- Any stack trace or error message.

---

## License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

You are free to use, copy, modify, merge, publish, distribute, sublicense, and sell copies of this software. Contributions back to the project are warmly encouraged but not required.

---

<p align="center">
  Made with ❤️ by the open-source community · <a href="https://github.com/sid-trip/NLP-to-JDBC">github.com/sid-trip/NLP-to-JDBC</a>
</p>