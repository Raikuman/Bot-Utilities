package com.raikuman.botutilities.database;

import com.raikuman.botutilities.configs.ConfigIO;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDataSource {

	private static final Logger logger = LoggerFactory.getLogger(SQLiteDataSource.class);
	private static final HikariConfig config = new HikariConfig();
	private static final HikariDataSource dataSource;

	static {
		try {
			final File databaseFile = new File("database.db");

			if (!databaseFile.exists())
				if (databaseFile.createNewFile())
					logger.info("Created database file");
				else
					logger.error("Could not create database file");

		} catch (IOException e) {
			e.printStackTrace();
		}

		config.setJdbcUrl("jdbc:sqlite:database.db");
		config.setConnectionTestQuery("SELECT 1");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		dataSource = new HikariDataSource(config);

		try (final Statement statement = getConnection().createStatement()) {
			final String defaultPrefix = ConfigIO.readConfig("settings", "prefix");

			// language=SQLITE-SQL
			statement.execute(
				"CREATE TABLE IF NOT EXISTS guild_settings (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"guild_id VARCHAR(20) NOT NULL," +
				"prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "')"
			);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private SQLiteDataSource() {}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
