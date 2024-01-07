package com.raikuman.botutilities.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    public static Connection getConnection() throws SQLException {
        return SQLiteDataSource.getConnection();
    }
}
