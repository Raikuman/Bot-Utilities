package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.entities.Message;
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

    public void addButtons(User user, Message message, List<ButtonComponent> buttonComponents) {
        HashMap<String, ButtonComponent> buttonMap = new HashMap<>();
        for (ButtonComponent buttonComponent : buttonComponents) {
            buttonMap.put(buttonComponent.getInvoke(), buttonComponent);
        }

        this.interactions.put(user, new ButtonInteraction(buttonMap, message, Instant.now()));
    }

    public void handleEvent(ButtonInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Check user
        User user = event.getUser();
        if (user.isBot()) {
            return;
        }

        // Split component id to get author and invocation
        String[] id = event.getComponentId().split(":");
        if (id.length != 2) {
            logger.error("Invalid button component id: " + event.getComponentId());
            return;
        }

        // Retrieve button
        ButtonInteraction interaction = interactions.get(user);
        if (interaction == null) {
            return;
        }

        ButtonComponent button = interaction.buttons.get(id[1]);
        if (button == null) {
            logger.error("Invalid button invocation: " + id[1]);
            return;
        }

        // Check author
        if (!button.ignoreAuthor() && !id[0].equals(user.getId())) {
            return;
        }

        // Handle button
        button.handle(event);
        interaction.updateInteraction();
    }

    public int trimButtons(boolean trimOnlyDeleted) {
        int amountTrimmed = interactions.size();

        for (Map.Entry<User, ButtonInteraction> entry : interactions.entrySet()) {
            ButtonInteraction buttonInteraction = entry.getValue();

            if (ComponentHandler.isTimedOut(buttonInteraction.getLastInteraction()) && !trimOnlyDeleted) {
                buttonInteraction.getMessage().delete().queue();
                interactions.remove(entry.getKey());
            } else {
                // Check if original message was deleted
                Message message = buttonInteraction.getMessage();
                MessageChannelUnion channel = message.getChannel();

                channel.retrieveMessageById(message.getId()).queue(null, new ErrorHandler() {
                    @Override
                    public void accept(Throwable t) {
                        // Empty handle
                    }
                }
                    .handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                        buttonInteraction.getMessage().delete().queue();
                        interactions.remove(entry.getKey());
                    }));
            }
        }

        return amountTrimmed - interactions.size();
    }

    public static class ButtonInteraction {
        private final HashMap<String, ButtonComponent> buttons;
        private final Message message;
        private Instant lastInteraction;

        public ButtonInteraction(HashMap<String, ButtonComponent> buttons, Message message, Instant lastInteraction) {
            this.buttons = buttons;
            this.message = message;
            this.lastInteraction = lastInteraction;
        }

        public HashMap<String, ButtonComponent> getButtons() {
            return buttons;
        }

        public Message getMessage() {
            return message;
        }

        public Instant getLastInteraction() {
            return lastInteraction;
        }

        public void updateInteraction() {
            lastInteraction = Instant.now();
        }
    }
}
