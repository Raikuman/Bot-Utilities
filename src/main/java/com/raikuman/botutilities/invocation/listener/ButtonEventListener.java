package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.invocation.manager.ButtonManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ButtonEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ButtonEventListener.class);
    private final ButtonManager manager;
    private final ExecutorService executor;

    public ButtonEventListener(ButtonManager manager, ExecutorService executor) {
        this.manager = manager;
        this.executor = executor;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + ButtonEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                manager.handleEvent(event);
            }
        });
    }
}
