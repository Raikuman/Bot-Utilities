package com.raikuman.botutilities.defaults.database;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.DefaultConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultDatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDatabaseStartup.class);

    public static void addGuild(Guild guild) {
        int guildId = getGuildId(guild);

        // Guild already exists, populate with new members
        if (guildId != -1) {
            populateUserMemberDB(guild.getMembers(), guildId);
            return;
        };

        // Build guild database
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO guild(guild_long) VALUES(?)"
            )) {
            statement.setLong(1, guild.getIdLong());
            statement.execute();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    guildId = resultSet.getInt(1);
                }
            }

            if (guildId == -1) throw new SQLException("Failed to return guild_id from table");
        } catch (SQLException e) {
            logger.error("An error occurred adding guild to database for: {}:{}", guild.getName(), guild.getId());
            return;
        }

        // Handle settings
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO guild_setting(guild_id) VALUES(?)"
            )) {
            statement.setInt(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred adding settings to database for: {}:{}", guild.getName(), guild.getId());
            return;
        }

        populateUserMemberDB(guild.getMembers(), guildId);
    }

    private static void populateUserMemberDB(List<Member> guildMembers, int guildId) {
        // Check for thin database
        boolean thinDatabase = Boolean.parseBoolean(new ConfigData(new DefaultConfig()).getConfig("thindatabase"));
        if (thinDatabase) return;

        // Build user database
        List<Integer> userIds = new ArrayList<>();
        for (Member member: guildMembers) {
            if (member.getUser().isBot()) continue;

            int userId = addUser(member.getUser());
            if (userId == -1) continue;

            userIds.add(userId);
        }

        // Build guild-member database
        for (Integer userId : userIds) {
            addMember(guildId, userId);
        }
    }

    public static void removeGuild(Guild guild) {
        int guildId = getGuildId(guild);

        // Guild does not exist
        if (guildId == -1) return;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM guild WHERE guild_id = ?"
            )) {
            statement.setString(1, String.valueOf(guildId));
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred deleting guild from database for: {}:{}", guild.getName(), guild.getId());
        }
    }

    public static int addUser(User user) {
        int userId = getUserId(user);

        // User already exists
        if (userId != -1) return userId;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user(user_long) VALUES(?)"
            )) {
            statement.setLong(1, user.getIdLong());
            statement.execute();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred adding user to database for: {}:{}", user.getEffectiveName(), user.getId());
        }

        return -1;
    }

    public static void removeUser(User user) {
        int userId = getUserId(user);

        // User does not exist
        if (userId == -1) return;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM user WHERE user_id = ?"
            )) {
            statement.setInt(1, userId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred deleting user from database for: {}:{}", user.getEffectiveName(), user.getId());
        }
    }

    public static void addMember(Integer guild, Integer user) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT guild_id FROM member WHERE user_id = ?"
            )) {
            statement.setInt(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (resultSet.getInt(1) == guild) {
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred retrieving user id using guild id for guild: {}, user: {}", guild, user);
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO member(guild_id, user_id) VALUES(?, ?)"
            )) {
            statement.setInt(1, guild);
            statement.setInt(2, user);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred adding member to database for guild: {}, user: {}", guild, user);
        }
    }

    public static void removeMember(Integer guild, Integer user) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM member WHERE guild_id = ? AND user_id = ?"
            )) {
            statement.setInt(1, guild);
            statement.setInt(2, user);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred deleting member from database for guild: {}, user: {}", guild, user);
        }
    }

    public static int getGuildId(Guild guild) {
        return getId(guild.getIdLong(), "SELECT guild_id FROM guild WHERE guild_long = ?");
    }

    public static int getUserId(User user) {
        return getId(user.getIdLong(), "SELECT user_id FROM user WHERE user_long = ?");
    }

    private static int getId(long id, String query) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                query
            )) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred getting id for: {}", id);
        }

        return -1;
    }

    public static String getPrefix(Guild guild) {
        int guildId = getGuildId(guild);

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT prefix FROM guild_setting WHERE guild_id = ?"
            )) {
            statement.setInt(1, guildId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred getting prefix for guild: {}", guildId);
        }

        return "";
    }
}
