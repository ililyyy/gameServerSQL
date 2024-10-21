package ch.seg.inf.unibe.gameserver.db.persistence;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseAccess {

    /**
     * Singleton instance of the database access.
     */
    private static DatabaseAccess instance;

    /**
     * Connection pooling for JDBC database connections.
     */
    private BasicDataSource dataSource;

    /**
     * The URL to connect to the database.
     */
    private String databaseURL;

    /**
     * Enable/Disable logging for database connections and SQL queries.
     */
    private Boolean log;

    public DatabaseAccess() {
        this.dataSource = new BasicDataSource();
        this.dataSource.setMinIdle(5);
        this.dataSource.setMaxIdle(10);
        this.dataSource.setMaxOpenPreparedStatements(100);
        this.dataSource.setMaxTotal(20);
    }

    public static DatabaseAccess getInstance() {
        if (DatabaseAccess.instance == null) {
            DatabaseAccess.instance = new DatabaseAccess();
        }
        return DatabaseAccess.instance;
    }

    public static void resetInstance() {
        DatabaseAccess.instance = null;
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close()  {
        // Properly shut down the connection pool:
        if (this.dataSource != null) {
            try {
                this.dataSource.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
        this.dataSource.setUrl(this.databaseURL);
    }

    public Boolean getLog() {
        return log;
    }

    public void setLog(Boolean log) {
        this.log = log;
    }

    /**
     * Connects to a database and sends the given update query.
     * In case of SQLite, the database file will be created if it doesn't already exist.
     *
     * @param query The query to be sent.
     */
    public void executeUpdate(String query) {
        if (!query.isEmpty()) {
            try (Connection connection = getConnection()) {
                DatabaseAccessUtil.executeUpdate(connection, query, this.log);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Connects to a database and sends the given query.
     * In case of SQLite, the database file will be created if it doesn't already exist.
     *
     * @param query       The query to be sent.
     * @return A ResultSet object that contains the data produced by the given query; or null.
     */
    public ConnectedResult executeQuery(String query) {
        if (!query.isEmpty()) {
            Connection connection = getConnection();
            return new ConnectedResult(DatabaseAccessUtil.executeQuery(connection, query, this.log), connection);
        }
        return null;
    }

    public static class ConnectedResult implements  AutoCloseable {

        public ResultSet resultSet;

        public Connection connection;

        public ConnectedResult(ResultSet resultSet, Connection connection) {
            this.resultSet = resultSet;
            this.connection = connection;
        }

        @Override
        public void close() {
            try {
                resultSet.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
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
     * @param insertionQuery The query to be sent.
     * @return The generated auto increment ID for an insertion or -1.
     */
    public Integer insertRow(String insertionQuery) {
        if (!insertionQuery.isEmpty()) {
            try (Connection connection = getConnection()) {
                return DatabaseAccessUtil.insertRow(connection, insertionQuery, this.log);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
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
     * @param insertionQuery The query to be sent.
     * @return The generated auto increment IDs for an insertion or an empty array.
     */
    public List<Integer> insertRows(String insertionQuery) {
        if (!insertionQuery.isEmpty()) {
            try (Connection connection = getConnection()) {
                return DatabaseAccessUtil.insertRows(connection, insertionQuery, this.log);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
