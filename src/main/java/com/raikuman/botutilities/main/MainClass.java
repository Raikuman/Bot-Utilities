package com.raikuman.botutilities.main;

import com.raikuman.botutilities.database.SQLiteDataSource;

import java.sql.SQLException;

public class MainClass {

	public static void main(String[] args) throws SQLException {
		SQLiteDataSource.getConnection();
	}
}
