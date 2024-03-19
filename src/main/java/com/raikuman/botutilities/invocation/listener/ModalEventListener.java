package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.invocation.manager.ModalManager;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ModalEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ModalEventListener.class);
    private final ModalManager manager;
    private final ExecutorService executor;

    public ModalEventListener(ModalManager manager, ExecutorService executor) {
        this.manager = manager;
        this.executor = executor;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + ModalEventListener.class.getName() + " is initialized", event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                manager.handleEvent(event);
            }
        });
    }
}
