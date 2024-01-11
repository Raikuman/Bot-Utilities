package com.raikuman.botutilities.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;
import java.time.Instant;

public class EmbedResources {

    public static EmbedBuilder error(String title, String description, MessageChannelUnion channel, User user) {
        return defaultResponse("#C10000", title, "❌" + description, channel, user);
    }

    public static EmbedBuilder success(String title, String description, MessageChannelUnion channel, User user) {
        return defaultResponse("#04D500", title, "✅ " + description, channel, user);
    }

    private static EmbedBuilder defaultResponse(String color, String title, String description,
                                                MessageChannelUnion channel, User user) {
        return new EmbedBuilder()
            .setColor(Color.decode(color))
            .setAuthor(title, user.getAvatarUrl(), user.getAvatarUrl())
            .setDescription(description)
            .setFooter("#" + channel.getName())
            .setTimestamp(Instant.now());
    }
}