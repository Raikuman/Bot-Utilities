package com.raikuman.botutilities.database;

import com.raikuman.botutilities.configs.DatabaseConfigInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Provides a connection to the datasource and creating tables based on config file statements
 *
 * @version 1.1 2022-16-07
 * @since 1.2
 */
public class DatabaseManager {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private static Connection connection = null;

	static {
		try {
			connection = SQLiteDataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(
			() -> {
				try {
					connection.close();
					System.out.println("Closing database connection");
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Could not close database connection");
				}
			}, "Shutdown-thread")
		);
	}

	/**
	 * Returns the database connection
	 * @return The connection from the database
	 */
	public static Connection getConnection() {
		return connection;
	}

	/**
	 * Executes statements from DatabaseConfigInterface classes to create tables
	 * @param configInterfaces The config interfaces to get statements from
	 */
	public static void executeConfigStatements(List<DatabaseConfigInterface> configInterfaces) {
		for (DatabaseConfigInterface configInterface : configInterfaces)
			for (String statement : configInterface.tableStatements())
				executeStatement(statement);
	}

	/**
	 * Execute a statement from connection to database
	 * @param statement Statement to execute
	 */
	private static void executeStatement(String statement) {
		try (Statement connectionStatement = connection.createStatement()) {
			connectionStatement.execute(statement);
		} catch (SQLException e) {
			logger.error("Could not create a statement for the database connection");
		}
	}
}
