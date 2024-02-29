package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.component.ComponentInteraction;
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

    public HashMap<User, SelectInteraction> getInteractions() {
        return interactions;
    }

    public void addSelects(User user, ComponentInteraction componentInteraction, List<SelectComponent> selectComponents) {
        HashMap<String, SelectComponent> selectMap = new HashMap<>();
        for (SelectComponent selectComponent : selectComponents) {
            selectMap.put(selectComponent.getInvoke(), selectComponent);
        }

        this.interactions.put(user, new SelectInteraction(selectMap, componentInteraction, Instant.now()));
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

            ComponentInteraction interaction = selectInteraction.getComponentInteraction();
            if (ComponentHandler.isTimedOut(selectInteraction.getLastInteraction()) && !trimOnlyDeleted) {
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

    public static class SelectInteraction {
        private final HashMap<String, SelectComponent> selects;
        private final ComponentInteraction componentInteraction;
        private Instant lastInteraction;

        public SelectInteraction(HashMap<String, SelectComponent> selects, ComponentInteraction componentInteraction, Instant lastInteraction) {
            this.selects = selects;
            this.componentInteraction = componentInteraction;
            this.lastInteraction = lastInteraction;
        }

        public HashMap<String, SelectComponent> getSelects() {
            return selects;
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
}
