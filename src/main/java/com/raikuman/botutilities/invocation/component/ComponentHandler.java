package com.raikuman.botutilities.invocation.component;

import com.raikuman.botutilities.invocation.listener.ButtonEventListener;
import com.raikuman.botutilities.invocation.manager.ButtonManager;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComponentHandler {

    private static final Logger logger = LoggerFactory.getLogger(ComponentHandler.class);
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ButtonManager buttonManager = new ButtonManager();
    private static final int TRIM_DELAY = 10;

    public ComponentHandler() {
        executorService.scheduleAtFixedRate(this::trimComponents, TRIM_DELAY, TRIM_DELAY, TimeUnit.MINUTES);
    }

    public void addButtons(User user, Message message, List<ButtonComponent> buttonComponents) {
        buttonManager.addButtons(user, message, buttonComponents);
    }

    public List<ListenerAdapter> getListeners(ExecutorService executor) {
        return List.of(
            new ButtonEventListener(buttonManager, executor)
        );
    }

    public static boolean isTimedOut(Instant lastInteraction) {
        return Instant.now().isAfter(lastInteraction.plus(TRIM_DELAY, TimeUnit.MINUTES.toChronoUnit()));
    }

    private void trimComponents() {
        int buttonsTrimmed = buttonManager.trimButtons();

        if (buttonsTrimmed == 0) {
            logger.info("No components trimmed");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Trimmed ");
            builder.append(buttonsTrimmed);

            logger.info(builder.toString());
        }
    }
}
