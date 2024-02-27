package com.raikuman.botutilities.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

public class MessageResources {

    public static void embedReplyDelete(Message message, int delay, EmbedBuilder embedBuilder) {
        message.replyEmbeds(
            embedBuilder.build()
        ).delay(Duration.ofSeconds(delay)).flatMap(Message::delete).queue();
    }
}
