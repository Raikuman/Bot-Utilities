package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.invocation.manager.CommandManager;
import com.raikuman.botutilities.invocation.type.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CommandEventListener.class);
    private final CommandManager manager;

    public CommandEventListener(List<Command> commands) {
        manager = new CommandManager(commands);
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + CommandEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        manager.handleEvent(event);
    }
}
