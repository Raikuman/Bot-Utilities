package com.raikuman.botutilities.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.time.Duration;

public class MessageResources {

    public static void embedReplyDelete(Message message, int delay, boolean deleteOriginal, EmbedBuilder embedBuilder) {
        message.replyEmbeds(
            embedBuilder.build()
        ).delay(Duration.ofSeconds(delay)).flatMap((reply) -> {
            if (deleteOriginal) {
                message.delete().queue();
            }

            return reply.delete();
        }).queue();
    }

    public static void embedDelete(MessageChannelUnion channel, int delay, EmbedBuilder embedBuilder) {
        channel.sendMessageEmbeds(
            embedBuilder.build()
        ).delay(Duration.ofSeconds(delay)).flatMap(Message::delete).queue();
    }
}
