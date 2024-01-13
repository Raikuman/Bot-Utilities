package com.raikuman.botutilities.invocation.component;

import com.raikuman.botutilities.invocation.listener.ButtonEventListener;
import com.raikuman.botutilities.invocation.listener.SelectEventListener;
import com.raikuman.botutilities.invocation.manager.ButtonManager;
import com.raikuman.botutilities.invocation.manager.SelectManager;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComponentHandler {

    private static final Logger logger = LoggerFactory.getLogger(ComponentHandler.class);
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ButtonManager buttonManager = new ButtonManager();
    private final SelectManager selectManager = new SelectManager();
    private static final int TRIM_DELAY = 10;

    public ComponentHandler() {
        executorService.scheduleAtFixedRate(this::trimComponents, TRIM_DELAY, TRIM_DELAY, TimeUnit.MINUTES);
    }

    public void addButtons(User user, Message message, List<ButtonComponent> buttonComponents) {
        buttonManager.addButtons(user, message, buttonComponents);
    }

    public void addSelects(User user, Message message, List<SelectComponent> selectComponents) {
        selectManager.addSelects(user, message, selectComponents);
    }

    public List<ListenerAdapter> getListeners(ExecutorService executor) {
        return List.of(
            new ButtonEventListener(buttonManager, executor),
            new SelectEventListener(selectManager, executor)
        );
    }

    public static boolean isTimedOut(Instant lastInteraction) {
        return Instant.now().isAfter(lastInteraction.plus(TRIM_DELAY, TimeUnit.MINUTES.toChronoUnit()));
    }

    private void trimComponents() {
        int buttonsTrimmed = buttonManager.trimButtons();
        int selectsTrimmed = selectManager.trimSelects();

        if (buttonsTrimmed == 0) {
            logger.info("No components trimmed");
            return;
        }

        List<String> trimStrings = new ArrayList<>();
        if (buttonsTrimmed > 0) {
            trimStrings.add(String.format("%d buttons trimmed", buttonsTrimmed));
        }

        if (selectsTrimmed > 0) {
            trimStrings.add(String.format("%d selects trimmed", selectsTrimmed));
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < trimStrings.size(); i++) {
            builder.append(trimStrings.get(i));

            if (i < trimStrings.size() - 1) {
                builder.append(", ");
            }
        }

        logger.info(builder.toString());
    }
}
