package com.raikuman.botutilities.invocation.context;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public record CommandContext(MessageReceivedEvent event, List<String> args) {
    public CommandContext {
        Objects.requireNonNull(event);
        Objects.requireNonNull(args);
    }
}
