package com.raikuman.botutilities.defaults.database;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DatabaseStartup;
import com.raikuman.botutilities.defaults.DefaultConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DefaultDatabaseStartup implements DatabaseStartup {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDatabaseStartup.class);

    @Override
    public void startup(JDA jda) {
        // Setup tables
        if (!setupTables()) return;

        populateTables(jda);
    }

    private boolean setupTables() {
        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
            ) {
            //  Guild table
            statement.addBatch(
                "CREATE TABLE IF NOT EXISTS guild(" +
                    "guild_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_long INTEGER UNIQUE NOT NULL" +
                    ")"
            );

            // Guild settings table
            statement.addBatch(
                "CREATE TABLE IF NOT EXISTS guild_setting(" +
                    "guild_setting_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id INTEGER UNIQUE NOT NULL," +
                    "prefix VARCHAR(20) NOT NULL DEFAULT '" + new ConfigData(new DefaultConfig()).getConfig("defaultprefix") + "'," +
                    "FOREIGN KEY(guild_id) REFERENCES guild(guild_id) ON DELETE CASCADE" +
                    ")"
            );

            // Check for thin database
            boolean thinDatabase = Boolean.parseBoolean(new ConfigData(new DefaultConfig()).getConfig("thindatabase"));
            if (thinDatabase) {
                statement.executeBatch();
                connection.close();
                return true;
            }

            // User table
            statement.addBatch(
                "CREATE TABLE IF NOT EXISTS user(" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_long INTEGER UNIQUE NOT NULL" +
                    ")"
            );

            // Guild-member table
            statement.addBatch(
                "CREATE TABLE IF NOT EXISTS member(" +
                    "member_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id INTEGER NOT NULL," +
                    "user_id INTEGER NOT NULL," +
                    "FOREIGN KEY(guild_id) REFERENCES guild(guild_id) ON DELETE CASCADE," +
                    "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE" +
                    ")"
            );

            statement.executeBatch();

            return true;
        } catch (SQLException e) {
            logger.error("An error occurred creating database tables");
            return false;
        }
    }

    private void populateTables(JDA jda) {
        for (Guild guild : jda.getGuilds()) {
            DefaultDatabaseHandler.addGuild(guild);
        }
    }
}
