package ch.seg.inf.unibe.gameserver.db.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DatabaseAccessUtil {

    public static boolean THROW_SQL_RUNTIME_EXCEPTIONS = true;

    // ANSI escape codes for text colors
    public static String RED = "\u001B[31m";
    public static String GREEN = "\u001B[32m";
    public static String RESET = "\u001B[0m";

    /**
     * Builds the URL for the connection to the SQLite database.
     * As SQLite is a file-based database the given directory will be created if it doesn't already exist.
     *
     * @param databasePath The folder to contain the SQLite database.
     * @param databaseFile The file name of the SQLite database.
     * @return The URL for the connection to the SQLite database.
     */
    public static String createSQLiteDatabaseURL(Path databasePath, String databaseFile) {
        try {
            // Creates the directory if it doesn't already exist:
            Files.createDirectories(databasePath);
        } catch (IOException e) {
            System.out.flush();
            System.err.println("IO Exception:" + e.getMessage());
            System.err.flush();
        }
        String urlPath = databasePath.toAbsolutePath().toString().replace("\\", "/");
        return "jdbc:sqlite:" + urlPath + "/" + databaseFile;
    }

    /**
     * Connects to a database and sends the given update query.
     * In case of SQLite, the database file will be created if it doesn't already exist.
     *
     * @param connection  The database connection.
     * @param query       The query to be sent.
     * @param log         Log the process (on the console).
     */
    public static void executeUpdate(Connection connection, String query, boolean log) {
        // Establish the connection
        try {
            if (log) {
                DatabaseMetaData meta = connection.getMetaData();
//                System.out.println("Connection established: " + meta.getDriverName());
                System.out.println(GREEN + "Send query: " + RESET + query.trim());
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);

//                if (log) {
//                    System.out.println("Done!\n\n");
//                }
            }
        } catch (SQLException e) {
            System.out.println(RED + "SQL Exception: " + e.getMessage() + RESET);
            System.out.println(RED + "To reset the database, delete the database file: " + DatabaseAccess.getInstance().getDatabaseURL() + RESET);
            if (THROW_SQL_RUNTIME_EXCEPTIONS) {
                throw new RuntimeException("SQL Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Connects to a database and sends the given query.
     * In case of SQLite, the database file will be created if it doesn't already exist.
     *
     * @param connection  The database connection.
     * @param query       The query to be sent.
     * @param log         Log the process (on the console).
     * @return A ResultSet object that contains the data produced by the given query; or null.
     */
    public static ResultSet executeQuery(Connection connection, String query, boolean log) {
        // Establish the connection
        try {
            if (log) {
                DatabaseMetaData meta = connection.getMetaData();
//                System.out.println("Connection established: " + meta.getDriverName());
                System.out.println(GREEN + "Send query: " + RESET + query.trim());
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

//            if (log) {
//                System.out.println("Done!\n\n");
//            }
            return resultSet;

        } catch (SQLException e) {
            System.out.println(RED + "SQL Exception: " + e.getMessage()  + RESET);
            System.out.println(RED + "To reset the database, delete the database file: " + DatabaseAccess.getInstance().getDatabaseURL() + RESET);
            if (THROW_SQL_RUNTIME_EXCEPTIONS) {
                throw new RuntimeException("SQL Exception: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Executes the given SQL query and returns the generated auto-increment ID.
     * The ID requires a table that specifies an auto-increment column.
     * <pre>
     * CREATE TABLE table_name (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * column_name TEXT,
     * column_name INTEGER,
     * );
     * </pre>
     * In this query, id is the auto-increment column which serves as the primary key.
     * The AUTOINCREMENT keyword ensures that each new row inserted into the table will be assigned a unique
     * and automatically incremented value for the id column.
     *
     * @param connection  The database connection.
     * @param insertionQuery The query to be sent.
     * @param log            Log the process (on the console).
     * @return The generated auto increment ID for an insertion or an empty array.
     */
    public static Integer insertRow(Connection connection, String insertionQuery, boolean log) {
        return insertRows(connection, insertionQuery, log).get(0);
    }

    /**
     * Executes the given SQL insertion query and returns the generated auto-increment IDs.
     * The IDs require a table that specifies an auto-increment column.
     * <pre>
     * CREATE TABLE table_name (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * column_name TEXT,
     * column_name INTEGER,
     * );
     * </pre>
     * In this query, id is the auto-increment column which serves as the primary key.
     * The AUTOINCREMENT keyword ensures that each new row inserted into the table will be assigned a unique
     * and automatically incremented value for the id column.
     *
     * @param connection  The database connection.
     * @param insertionQuery The query to be sent.
     * @param log            Log the process (on the console).
     * @return The generated auto increment IDs for an insertion or an empty array.
     */
    public static List<Integer> insertRows(Connection connection, String insertionQuery, boolean log) {
        // Establish the connection
        try {
            if (log) {
                DatabaseMetaData meta = connection.getMetaData();
//                System.out.println("Connection established: " + meta.getDriverName());
                System.out.println(GREEN + "Send query: " + RESET + insertionQuery.trim());
            }

            try (Statement statement = connection.createStatement()) {
                int rowsAffected = statement.executeUpdate(insertionQuery, Statement.RETURN_GENERATED_KEYS);

//                if (log) {
//                    System.out.println("Done!\n\n");
//                }

                if (rowsAffected > 0) {
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        List<Integer> ids = new ArrayList<>();
                        while (keys.next()) {
                            int id = keys.getInt(1);
                            ids.add(id);

//                            if (log) {
//                                System.out.println("New ID: " + id);
//                            }
                        }
                        return ids;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(RED + "SQL Exception: " + e.getMessage() + RESET);
            System.out.println(RED + "To reset the database, delete the database file: " + DatabaseAccess.getInstance().getDatabaseURL() + RESET);
            if (THROW_SQL_RUNTIME_EXCEPTIONS) {
                throw new RuntimeException("SQL Exception: " + e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Evaluated the given expression and returns a String representation of the result.
     * Returns the String "NULL" on a NullPointerException.
     * (see safe navigation operator)
     *
     * @param expression Expression to be evaluated.
     * @return The toString result of the expression or the String "NULL".
     */
    public static String valueOf(Supplier expression) {
        try {
            return expression.get().toString();
        } catch (NullPointerException e) {
            return "NULL";
        }
    }

    /**
     * Converts a Java data (Instant) to a SQL date (Timestamp).
     *
     * @param instant Java date.
     * @return SQL data.
     */
    public static String instantToSQLTimestamp(Instant instant) {
        return Timestamp.from(instant).toString();
    }

    /**
     * Converts a SQL date (Timestamp) to a Java data (Instant).
     *
     * @param timestamp SQL date.
     * @return Java data.
     */
    public static Instant sqlTimestampToInstant(Timestamp timestamp) {
        return timestamp.toInstant();
    }

}
