package com.raikuman.botutilities.defaults.invocation;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.type.Slash;
import com.raikuman.botutilities.utilities.EmbedResources;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;

public class Prefix extends Slash {

    @Override
    public void handle(SlashCommandInteractionEvent ctx) {
        OptionMapping optionMapping = ctx.getOption("prefix");

        // Handle null mapping
        if (optionMapping == null) {
            ctx.replyEmbeds(
                EmbedResources.error(
                    "Error getting option",
                    "Could not retrieve option from the command",
                    ctx.getChannel(),
                    ctx.getUser()
                ).build())
                .delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        String prefix = optionMapping.getAsString();

        // Handle prefix db success
        if (updatePrefix(prefix, ctx.getGuild())) {
            ctx.replyEmbeds(
                EmbedResources.success(
                    "Successfully changed prefix",
                    "Prefix has been changed to `" + prefix + "`",
                    ctx.getChannel(),
                    ctx.getUser()
                ).build())
                .delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
        } else {
            ctx.replyEmbeds(
                EmbedResources.error(
                    "Error updating prefix",
                    "Could not update prefix",
                    ctx.getChannel(),
                    ctx.getUser()
                ).build())
                .delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
        }
    }

    @Override
    public String getInvoke() {
        return "prefix";
    }

    @Override
    public String getDescription() {
        return "Update the current prefix for the server";
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getInvoke(), getDescription())
            .addOption(
                OptionType.STRING,
                "prefix",
                "The new prefix for the server",
                true
            );
    }

    @Override
    public Category getCategory() {
        return new Settings();
    }

    private boolean updatePrefix(String prefix, Guild guild) {
        int guildId = DefaultDatabaseHandler.getGuildId(guild);
        if (guildId == -1) {
            return false;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE guild_setting SET prefix = ? WHERE guild_id = ?"
            )) {
            statement.setString(1, prefix);
            statement.setInt(2, guildId);
            statement.execute();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
