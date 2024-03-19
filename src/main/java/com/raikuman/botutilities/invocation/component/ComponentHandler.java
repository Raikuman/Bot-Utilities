package com.raikuman.botutilities.invocation.component;

import com.raikuman.botutilities.invocation.listener.ButtonEventListener;
import com.raikuman.botutilities.invocation.listener.ModalEventListener;
import com.raikuman.botutilities.invocation.listener.SelectEventListener;
import com.raikuman.botutilities.invocation.manager.ButtonManager;
import com.raikuman.botutilities.invocation.manager.ModalManager;
import com.raikuman.botutilities.invocation.manager.SelectManager;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.ModalComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComponentHandler {

    private static final Logger logger = LoggerFactory.getLogger(ComponentHandler.class);
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ButtonManager buttonManager = new ButtonManager();
    private final SelectManager selectManager = new SelectManager();
    private final ModalManager modalManager = new ModalManager();
    private static final int TRIM_DELAY = 10;

    public ComponentHandler() {
        executorService.scheduleAtFixedRate(this::trimComponents, TRIM_DELAY, TRIM_DELAY, TimeUnit.MINUTES);
    }

    public void addButtons(User user, Message message, List<ButtonComponent> buttonComponents) {
        updateButtonHandlers(buttonComponents);
        buttonManager.addButtons(user, new ComponentInteraction(message, null), buttonComponents);
    }

    public void addButtons(User user, Message message, ButtonComponent... buttonComponents) {
        List<ButtonComponent> buttonComponentList = List.of(buttonComponents);
        updateButtonHandlers(buttonComponentList);
        buttonManager.addButtons(user, new ComponentInteraction(message, null), buttonComponentList);
    }

    public void addButtons(User user, InteractionHook hook, List<ButtonComponent> buttonComponents) {
        updateButtonHandlers(buttonComponents);
        buttonManager.addButtons(user, new ComponentInteraction(null, hook), buttonComponents);
    }

    public void addButtons(User user, InteractionHook hook, ButtonComponent... buttonComponents) {
        List<ButtonComponent> buttonComponentList = List.of(buttonComponents);
        updateButtonHandlers(buttonComponentList);
        buttonManager.addButtons(user, new ComponentInteraction(null, hook), buttonComponentList);
    }

    public void addSelects(User user, Message message, List<SelectComponent> selectComponents) {
        updateSelectHandlers(selectComponents);
        selectManager.addSelects(user, new ComponentInteraction(message, null), selectComponents);
    }

    public void addSelects(User user, Message message, SelectComponent... selectComponents) {
        List<SelectComponent> selectComponentList = List.of(selectComponents);
        updateSelectHandlers(selectComponentList);
        selectManager.addSelects(user, new ComponentInteraction(message, null), selectComponentList);
    }

    public void addSelects(User user, InteractionHook hook, List<SelectComponent> selectComponents) {
        updateSelectHandlers(selectComponents);
        selectManager.addSelects(user, new ComponentInteraction(null, hook), selectComponents);
    }

    public void addSelects(User user, InteractionHook hook, SelectComponent... selectComponents) {
        List<SelectComponent> selectComponentList = List.of(selectComponents);
        updateSelectHandlers(selectComponentList);
        selectManager.addSelects(user, new ComponentInteraction(null, hook), selectComponentList);
    }

    public void addModal(ModalComponent modalComponent) {
        modalComponent.componentHandler = this;
        modalManager.addModal(modalComponent);
    }

    public List<ListenerAdapter> getListeners(ExecutorService executor) {
        return List.of(
            new ButtonEventListener(buttonManager, executor),
            new SelectEventListener(selectManager, executor),
            new ModalEventListener(modalManager, executor)
        );
    }

    public static boolean isTimedOut(Instant lastInteraction) {
        return Instant.now().isAfter(lastInteraction.plus(TRIM_DELAY, TimeUnit.MINUTES.toChronoUnit()));
    }

    private void trimComponents() {
        int buttonsTrimmed = buttonManager.trimButtons(false);
        int selectsTrimmed = selectManager.trimSelects(false);

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

    private void updateButtonHandlers(List<ButtonComponent> buttonComponents) {
        for (ButtonComponent buttonComponent : buttonComponents) {
            buttonComponent.componentHandler = this;
        }
    }

    private void updateSelectHandlers(List<SelectComponent> selectComponents) {
        for (SelectComponent selectComponent : selectComponents) {
            selectComponent.componentHandler = this;
        }
    }
}
