package com.raikuman.botutilities.utilities;

import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;
import java.time.Instant;

public class EmbedResources {

    public static EmbedBuilder error(String title, String description, MessageChannelUnion channel, User user) {
        return defaultResponse(Color.decode("#C10000"), title, "❌ " + description, channel, user);
    }

    public static EmbedBuilder success(String title, String description, MessageChannelUnion channel, User user) {
        return defaultResponse(Color.decode("#04D500"), title, "✅ " + description, channel, user);
    }

    public static EmbedBuilder incorrectUsage(String invoke, String usage, MessageChannelUnion channel) {
        String prefix = DefaultDatabaseHandler.getPrefix(channel.asGuildMessageChannel().getGuild());

        return defaultResponse(
            Color.decode("#C10000"),
            "Incorrect usage for " + prefix + invoke,
            "Usage: `" + prefix + invoke + " " + usage + "`",
            channel,
            channel.getJDA().getSelfUser()
        );
    }

    public static EmbedBuilder defaultResponse(Color color, String title, String description,
                                                MessageChannelUnion channel, User user) {
        return new EmbedBuilder()
            .setColor(color)
            .setAuthor(title, null, user.getEffectiveAvatarUrl())
            .setDescription(description)
            .setFooter("#" + channel.getName())
            .setTimestamp(Instant.now());
    }
}
