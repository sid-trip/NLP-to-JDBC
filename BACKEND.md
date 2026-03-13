# Backend Architecture Documentation: NLP-to-SQL

## 1. Executive Summary
The **NLP-to-SQL** project is a Java-based middleware engine designed to translate natural language user queries into executable SQL statements. Rather than relying on static, hardcoded queries, the backend dynamically introspects the target database schema at runtime. It utilizes a multi-stage lexical pipeline and dynamic programming algorithms to handle semantic mapping, entity resolution, and query synthesis.

## 2. Core Modules and System Flow

The backend operates primarily through two decoupled components: the Data Access component (`DatabaseManager.java`) and the Core Processing Unit (`SemanticParser.java`).

The standard execution flow is as follows:
1. The system connects to the database and extracts structural metadata (Tables and Columns).
2. The metadata is loaded into memory as a lookup matrix.
3. Natural language input is sanitized, tokenized, and evaluated against the in-memory schema using fuzzy string matching.
4. Resolved entities are synthesized into a standard SQL `SELECT` statement.

---

## 3. Module Breakdown and Internal Mechanics

### 3.1 Data Access Layer (`DatabaseManager.java`)
This module is responsible for database connectivity and dynamic schema extraction. By abstracting the schema retrieval, the application remains database-agnostic and scalable.

*   **`getConnection()`**
    *   **Purpose:** Establishes a secure connection to the MariaDB instance.
    *   **How it works:** It dynamically loads the MySQL JDBC Driver (`com.mysql.cj.jdbc.Driver`) into the JVM using `Class.forName()`. It then leverages the `DriverManager` to open a socket connection using the provided URL and credentials, returning a highly cohesive `Connection` object.
*   **`getFullSchema()`**
    *   **Purpose:** Bootstraps the application's vocabulary by mapping the target database structure.
    *   **How it works:** It calls `Connection.getMetaData()` to retrieve a `DatabaseMetaData` object. First, it queries the metadata for all tables within the target catalog, returning a `ResultSet`. For every table identified, a nested metadata query retrieves all associated column names. These are aggregated into a `Map<String, List<String>>` where the key is the table name and the value is a list of its columns. This in-memory map is critical for the parser's entity resolution phase.

### 3.2 The Core Processing Unit (`SemanticParser.java`)
This is the primary algorithmic engine. It uses a rule-based heuristic approach combined with mathematical string similarity logic.

*   **`SemanticParser(Map<String, List<String>> databaseSchema)` (Constructor)**
    *   **Purpose:** Initializes the processing environment.
    *   **How it works:** It accepts the schema map via dependency injection. It then populates a `HashMap` mapping English comparative words (e.g., "greater", "is", "above") to their respective SQL operators (e.g., `>`, `=`). Additionally, it populates a `List` of stop-words—syntactic noise (e.g., "the", "please") that hold no structural value in SQL generation.

*   **`parse(String input)`**
    *   **Purpose:** The main orchestration method that converts natural language into SQL.
    *   **How it works:** The execution occurs in four distinct phases:
        1.  **Phase 1: Phrase Normalization:** Multi-word comparative phrases (e.g., "more than", "equal to") are standardized into single-word internal tokens (e.g., "greater", "equal") using string replacement. This preserves the semantic intent before tokenization breaks the string apart.
        2.  **Phase 2: Lexical Tokenization:** The input is sanitized using a Regular Expression (`[^a-zA-Z0-9_ ]`) to strip arbitrary punctuation while preserving underscores (critical for database identifiers like `emp_id`). The string is then split into a token array via whitespace delimiters (`\s+`).
        3.  **Phase 3: Entity Resolution:** The engine iterates through the tokens. It skips recognized stop-words. It evaluates the remaining tokens against the in-memory schema map using the `levenshtein_similarity` function. If a token matches a table or column name with a similarity coefficient > 0.8, it is assigned to the respective SQL bucket (`tableFound`, `colFound`). Operators are mapped via the dictionary, and numeric/string literal values are isolated via regex digit matching and fallback logic.
        4.  **Phase 4: Synthesis:** The resolved variables are concatenated into a valid SQL string. Fallback logic dictates that if a specific column or condition isn't resolved, it defaults to a standard `SELECT * FROM [table]` query.

*   **`levenshtein_similarity(String s1, String s2)`**
    *   **Purpose:** Provides fault tolerance for user input (typo handling) via fuzzy matching.
    *   **How it works:** It implements the Levenshtein Distance algorithm using dynamic programming. A 2D array (matrix) of size `[n+1][m+1]` is initialized. The algorithm calculates the minimum number of single-character edits (insertions, deletions, or substitutions) required to mutate string `s1` into string `s2`. The raw distance is then normalized into a percentage coefficient (0.0 to 1.0) by subtracting the ratio of the distance to the maximum string length from 1.

*   **`recoverCase(String word, String original)`**
    *   **Purpose:** Ensures data integrity for string literals in the generated SQL.
    *   **How it works:** Because the parsing engine converts all input to lowercase for uniform processing, string literals (e.g., proper nouns like "Bangalore") lose their case sensitivity, which can cause SQL `WHERE` clauses to fail. This function iterates through the original, un-mutated user string, applies the same regex sanitization, and maps the normalized token back to its original case-preserved counterpart.

## 4. Technical Specifications
*   **Architecture Pattern:** Component-based Middleware
*   **Language:** Java (JDK 17)
*   **Database Engine:** MariaDB (accessed via MySQL Connector/J)
*   **Time Complexity (Parsing):** $O(T \times (N \times M))$, where $T$ is the number of tokens, and $N \times M$ represents the Levenshtein matrix dimensions during schema evaluation.

