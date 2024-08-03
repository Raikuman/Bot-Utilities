package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.component.ComponentInteraction;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonManager {

    private static final Logger logger = LoggerFactory.getLogger(ButtonManager.class);
    private final HashMap<User, List<ButtonInteraction>> interactions = new HashMap<>();

    public HashMap<User, List<ButtonInteraction>> getInteractions() {
        return interactions;
    }

    public void addButtons(User user, ComponentInteraction componentInteraction, List<ButtonComponent> buttonComponents) {
        HashMap<String, ButtonComponent> buttonMap = new HashMap<>();
        for (ButtonComponent buttonComponent : buttonComponents) {
            buttonMap.put(buttonComponent.getInvoke(), buttonComponent);
        }

        this.interactions.computeIfAbsent(user, k -> new ArrayList<>());
        this.interactions.get(user).add(new ButtonInteraction(buttonMap, componentInteraction, Instant.now()));
    }

    public void handleEvent(ButtonInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Check if bot
        if (event.getUser().isBot()) {
            return;
        }

        // Get original user from button id
        if (event.getButton().getId() == null) {
            logger.error("Could not find id of button");
            return;
        }

        // Check user
        String userId = event.getButton().getId().split(":")[0];
        User originalUser = event.getJDA().getUserById(userId);
        User invoker = event.getUser();
        if (originalUser == null) {
            logger.error("Could not retrieve user from JDA using: {}", userId);
            return;
        }

        // Split component id to get author and invocation
        String[] id = event.getComponentId().split(":");
        if (id.length != 2) {
            logger.error("Invalid button component id: {}", event.getComponentId());
            return;
        }

        InteractionComponents interaction = retrieveInteraction(originalUser, invoker, id[1]);
        if (interaction == null) {
            return;
        }

        interaction.handle(event, invoker.getId(), id[0]);
    }

    private InteractionComponents retrieveInteraction(User original, User invoker, String buttonId) {
        // Retrieve interaction via invoker
        List<ButtonInteraction> buttonInteractions = interactions.get(invoker);
        if (buttonInteractions == null) {
            // Retrieve interaction via original
            buttonInteractions = interactions.get(original);
            if (buttonInteractions == null) {
                return null;
            }
        }

        // Retrieve button via interaction
        ButtonComponent buttonComponent = null;
        ButtonInteraction buttonInteraction = null;
        for (ButtonInteraction interaction : buttonInteractions) {
            buttonComponent = interaction.getButtons().get(buttonId);
            if (buttonComponent != null) {
                buttonInteraction = interaction;
                break;
            }
        }

        if (buttonComponent == null) {
            logger.error("Invalid button id: {}", buttonId);
            return null;
        }

        return new InteractionComponents(buttonComponent, buttonInteraction);
    }

    public int trimButtons(boolean trimOnlyDeleted) {
        int amountTrimmed = interactions.size();

        for (Map.Entry<User, List<ButtonInteraction>> entry : interactions.entrySet()) {
            for (ButtonInteraction buttonInteraction : entry.getValue()) {
                ComponentInteraction interaction = buttonInteraction.getComponentInteraction();
                if (ComponentHandler.isTimedOut(buttonInteraction.getLastInteraction()) && !trimOnlyDeleted) {
                    if (interaction.message() != null) {
                        interaction.message().delete().queue();
                    } else if (interaction.hook() != null) {
                        interaction.hook().deleteOriginal().queue();
                    }

                    interactions.remove(entry.getKey());
                } else {
                    if (interaction.message() != null) {
                        // Check if original message was deleted
                        MessageChannelUnion channel = interaction.message().getChannel();
                        channel.retrieveMessageById(interaction.message().getId()).queue(null, new ErrorHandler() {
                            @Override
                            public void accept(Throwable t) {
                                // Empty handle
                            }
                        }.handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                            interaction.message().delete().queue();
                            interactions.remove(entry.getKey());
                        }));
                    } else if (interaction.hook() != null && interaction.hook().isExpired()) {
                        interaction.hook().deleteOriginal().queue();
                    }
                }
            }
        }

        return amountTrimmed - interactions.size();
    }

    public static class ButtonInteraction {
        private final HashMap<String, ButtonComponent> buttons;
        private final ComponentInteraction componentInteraction;
        private Instant lastInteraction;

        public ButtonInteraction(HashMap<String, ButtonComponent> buttons, ComponentInteraction componentInteraction, Instant lastInteraction) {
            this.buttons = buttons;
            this.componentInteraction = componentInteraction;
            this.lastInteraction = lastInteraction;
        }

        public HashMap<String, ButtonComponent> getButtons() {
            return buttons;
        }

        public ComponentInteraction getComponentInteraction() {
            return componentInteraction;
        }

        public Instant getLastInteraction() {
            return lastInteraction;
        }

        public void updateInteraction() {
            lastInteraction = Instant.now();
        }
    }

    static class InteractionComponents {
        private final ButtonComponent buttonComponent;
        private final ButtonInteraction buttonInteraction;

        public InteractionComponents(ButtonComponent buttonComponent, ButtonInteraction buttonInteraction) {
            this.buttonComponent = buttonComponent;
            this.buttonInteraction = buttonInteraction;
        }

        public void handle(ButtonInteractionEvent event, String buttonAuthorId, String invokerId) {
            // Check author
            if (!buttonComponent.ignoreAuthor() && !buttonAuthorId.equals(invokerId)) {
                event.deferEdit().queue();
                return;
            }

            // Handle button
            buttonComponent.handle(event);
            buttonInteraction.updateInteraction();
        }
    }
}
