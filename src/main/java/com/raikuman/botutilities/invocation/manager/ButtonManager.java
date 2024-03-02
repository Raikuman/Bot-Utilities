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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonManager {

    private static final Logger logger = LoggerFactory.getLogger(ButtonManager.class);
    private final HashMap<User, ButtonInteraction> interactions = new HashMap<>();

    public HashMap<User, ButtonInteraction> getInteractions() {
        return interactions;
    }

    public void addButtons(User user, ComponentInteraction componentInteraction, List<ButtonComponent> buttonComponents) {
        HashMap<String, ButtonComponent> buttonMap = new HashMap<>();
        for (ButtonComponent buttonComponent : buttonComponents) {
            buttonMap.put(buttonComponent.getInvoke(), buttonComponent);
        }

        this.interactions.put(user, new ButtonInteraction(buttonMap, componentInteraction, Instant.now()));
    }

    public void handleEvent(ButtonInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Check user
        User invoker = event.getUser();
        User originalUser;
        if (event.getMessage().getInteraction() == null) {
            originalUser = event.getMessage().getAuthor();
        } else {
            originalUser = event.getMessage().getInteraction().getUser();
        }
        if (invoker.isBot() || originalUser.isBot()) {
            return;
        }

        // Split component id to get author and invocation
        String[] id = event.getComponentId().split(":");
        if (id.length != 2) {
            logger.error("Invalid button component id: " + event.getComponentId());
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
        ButtonInteraction buttonInteraction = interactions.get(invoker);
        if (buttonInteraction == null) {
            // Retrieve interaction via original
            buttonInteraction = interactions.get(original);
            if (buttonInteraction == null) {
                return null;
            }
        }

        // Retrieve button via interaction
        ButtonComponent buttonComponent = buttonInteraction.getButtons().get(buttonId);
        if (buttonComponent == null) {
            logger.error("Invalid button id: " + buttonId);
            return null;
        }

        return new InteractionComponents(buttonComponent, buttonInteraction);
    }

    public int trimButtons(boolean trimOnlyDeleted) {
        int amountTrimmed = interactions.size();

        for (Map.Entry<User, ButtonInteraction> entry : interactions.entrySet()) {
            ButtonInteraction buttonInteraction = entry.getValue();

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
                return;
            }

            // Handle button
            buttonComponent.handle(event);
            buttonInteraction.updateInteraction();
        }
    }
}
