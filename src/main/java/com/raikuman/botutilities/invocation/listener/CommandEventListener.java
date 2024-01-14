package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.manager.CommandManager;
import com.raikuman.botutilities.invocation.type.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class CommandEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CommandEventListener.class);
    private final CommandManager manager;
    private final ExecutorService executor;

    public CommandEventListener(List<Command> commands, ExecutorService executor, ComponentHandler componentHandler,
                                boolean disableDatabase) {
        manager = new CommandManager(commands, componentHandler, disableDatabase);
        this.executor = executor;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + CommandEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                manager.handleEvent(event);
            }
        });
    }
}
