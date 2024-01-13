package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.invocation.manager.SelectManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class SelectEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SelectEventListener.class);
    private final SelectManager manager;
    private final ExecutorService executor;

    public SelectEventListener(SelectManager manager, ExecutorService executor) {
        this.manager = manager;
        this.executor = executor;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + SelectEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                manager.handleEvent(event);
            }
        });
    }
}
