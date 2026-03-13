# NLIDB Backend Documentation: Semantic SQL Engine

## 1. Overview
The backend of this project is a **Metadata-Driven Natural Language Interface for Databases (NLIDB)**. Unlike traditional applications that use hardcoded SQL queries, this engine dynamically interprets user intent and constructs SQL queries based on the real-time schema of the connected MariaDB instance.

## 2. Core Components

### A. Data Access Layer (`DatabaseManager.java`)
This component handles the "Connectivity" and "Introspection" of the database.
- **JDBC Integration:** Uses the MySQL Connector/J driver to establish a bridge to MariaDB.
- **Dynamic Metadata Discovery:** Implements `DatabaseMetaData` to scan the database catalog.
- **Schema Mapping:** The `getFullSchema()` method programmatically retrieves all table names and their respective column names, storing them in a `Map<String, List<String>>`. This ensures the engine is **schema-agnostic** (it works with any database without code changes).

### B. Semantic Engine (`SemanticParser.java`)
The "Brain" of the system. It processes raw English strings into valid SQL via a 4-stage pipeline:

1. **Normalization & Phrase Mapping:**
   Converts multi-word human expressions (e.g., "more than", "located in") into standard internal tokens (e.g., "above", "in") to prevent semantic loss during tokenization.
2. **Lexical Analysis:**
   Uses Regular Expressions (Regex) to sanitize input and split strings into tokens, preserving underscores for database compatibility.
3. **Fuzzy Entity Matching (Levenshtein Algorithm):**
   Implements the **Levenshtein Distance** algorithm to calculate the edit distance between user words and database entities. This allows the system to handle typos (e.g., "salry" matches "salary" with >80% similarity).
4. **Query Synthesis:**
   Heuristically categorizes tokens into Table, Column, Operator, and Value buckets to construct the final SQL string.

## 3. Key Algorithms & Technologies
- **Algorithm:** Levenshtein Distance (Dynamic Programming) for fuzzy string matching.
- **Regex:** `[^a-zA-Z0-9_ ]` for non-destructive data cleaning.
- **Data Structures:** `HashMap` for $O(1)$ operator lookup; `ArrayList` for schema storage.
- **Environment:** Arch Linux, OpenJDK 17, MariaDB 11.x, Apache Tomcat 9.0.

## 4. Technical Constraints
- **Statelessness:** The engine does not store user state, ensuring high performance.
- **Case Sensitivity:** Implements a `recoverCase` utility to ensure SQL string literals match the original user input for case-sensitive database lookups.