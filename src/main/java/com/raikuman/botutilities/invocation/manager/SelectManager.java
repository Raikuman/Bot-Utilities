package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectManager {

    private static final Logger logger = LoggerFactory.getLogger(SelectManager.class);
    private final HashMap<User, SelectInteraction> interactions = new HashMap<>();

    public void addSelects(User user, Message message, List<SelectComponent> selectComponents) {
        HashMap<String, SelectComponent> selectMap = new HashMap<>();
        for (SelectComponent selectComponent : selectComponents) {
            selectMap.put(selectComponent.getInvoke(), selectComponent);
        }

        this.interactions.put(user, new SelectInteraction(selectMap, message, Instant.now()));
    }

    public void handleEvent(StringSelectInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Check user
        User user = event.getUser();
        if (user.isBot()) {
            return;
        }

        // Retrieve invocation string
        if (event.getValues().size() != 1) {
            return;
        }
        String invoke = event.getValues().get(0);

        // Split component id to get author and invocation
        String[] id = event.getComponentId().split(":");
        if (id.length != 2) {
            logger.error("Invalid select component id: " + event.getComponentId());
            return;
        }

        // Check author
        if (!id[0].equals(user.getId())) {
            return;
        }

        // Retrieve select
        SelectInteraction interaction = interactions.get(user);
        if (interaction == null) {
            return;
        }

        SelectComponent select = interaction.selects.get(invoke);
        if (select == null) {
            logger.error("Invalid select invocation: " + invoke);
            return;
        }

        // Handle select
        select.handle(event);
        interaction.updateInteraction();
    }

    public int trimSelects(boolean trimOnlyDeleted) {
        int amountTrimmed = interactions.size();

        for (Map.Entry<User, SelectInteraction> entry : interactions.entrySet()) {
            SelectInteraction selectInteraction = entry.getValue();

            if (ComponentHandler.isTimedOut(selectInteraction.getLastInteraction()) && !trimOnlyDeleted) {
                selectInteraction.getMessage().delete().queue();
                interactions.remove(entry.getKey());
            } else {
                // Check if original message was deleted
                Message message = selectInteraction.getMessage();
                MessageChannelUnion channel = message.getChannel();

                channel.retrieveMessageById(message.getId()).queue(null, new ErrorHandler() {
                    @Override
                    public void accept(Throwable t) {
                        // Empty handle
                    }
                }
                    .handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                        selectInteraction.getMessage().delete().queue();
                        interactions.remove(entry.getKey());
                    }));
            }
        }

        return amountTrimmed - interactions.size();
    }

    static class SelectInteraction {
        private final HashMap<String, SelectComponent> selects;
        private final Message message;
        private Instant lastInteraction;

        public SelectInteraction(HashMap<String, SelectComponent> selects, Message message, Instant lastInteraction) {
            this.selects = selects;
            this.message = message;
            this.lastInteraction = lastInteraction;
        }

        public void updateInteraction() {
            lastInteraction = Instant.now();
        }

        public Instant getLastInteraction() {
            return lastInteraction;
        }

        public Message getMessage() {
            return message;
        }
    }
}
