package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private final static String JDBC_DATABASE_URL;
    private final static String JDBC_DATABASE_USERNAME;
    private final static String JDBC_DATABASE_PASSWORD;

    static {
        JDBC_DATABASE_URL = System.getenv("JDBC_DATABASE_URL");
        JDBC_DATABASE_USERNAME = System.getenv("JDBC_DATABASE_USERNAME");
        JDBC_DATABASE_PASSWORD = System.getenv("JDBC_DATABASE_PASSWORD");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_DATABASE_URL, JDBC_DATABASE_USERNAME,
                JDBC_DATABASE_PASSWORD);
    }
}
