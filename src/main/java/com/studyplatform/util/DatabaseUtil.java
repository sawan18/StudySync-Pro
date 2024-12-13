package com.studyplatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_URL = "jdbc:derby:studydb;create=true;";

    // Static initializer to load Derby driver
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Derby JDBC Driver not found: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseUtil() {}

    // Always return a new connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
