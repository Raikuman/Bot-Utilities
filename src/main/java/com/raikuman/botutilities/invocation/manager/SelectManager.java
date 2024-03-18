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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectManager {

    private static final Logger logger = LoggerFactory.getLogger(SelectManager.class);
    private final HashMap<User, List<SelectInteraction>> interactions = new HashMap<>();

    public HashMap<User, List<SelectInteraction>> getInteractions() {
        return interactions;
    }

    public void addSelects(User user, ComponentInteraction componentInteraction, List<SelectComponent> selectComponents) {
        HashMap<String, SelectComponent> selectMap = new HashMap<>();
        for (SelectComponent selectComponent : selectComponents) {
            selectMap.put(selectComponent.getInvoke(), selectComponent);
        }

        this.interactions.computeIfAbsent(user, k -> new ArrayList<>());
        this.interactions.get(user).add(new SelectInteraction(selectMap, componentInteraction, Instant.now()));
    }

    public void handleEvent(StringSelectInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Check if bot
        if (event.getUser().isBot()) {
            return;
        }

        // Check user
        String userId = event.getComponentId().split(":")[0];
        User originalUser = event.getJDA().getUserById(userId);
        User invoker = event.getUser();
        if (originalUser == null) {
            logger.error("Could not retrieve user from JDA using: " + userId);
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

        InteractionComponents interaction = retrieveInteraction(originalUser, invoker, invoke);
        if (interaction == null) {
            return;
        }

        interaction.handle(event, invoker.getId(), id[0]);
    }

    private InteractionComponents retrieveInteraction(User original, User invoker, String selectId) {
        // Retrieve interaction via invoker
        List<SelectInteraction> selectInteractions = interactions.get(invoker);
        if (selectInteractions == null) {
            // Retrieve interaction via original
            selectInteractions = interactions.get(original);
            if (selectInteractions == null) {
                return null;
            }
        }

        // Retrieve select via interaction
        SelectComponent selectComponent = null;
        SelectInteraction selectInteraction = null;
        for (SelectInteraction interaction : selectInteractions) {
            selectComponent = interaction.getSelects().get(selectId);
            if (selectComponent != null) {
                selectInteraction = interaction;
                break;
            }
        }

        if (selectComponent == null) {
            logger.error("Invalid select invocation: " + selectId);
            return null;
        }

        return new InteractionComponents(selectComponent, selectInteraction);
    }

    public int trimSelects(boolean trimOnlyDeleted) {
        int amountTrimmed = interactions.size();

        for (Map.Entry<User, List<SelectInteraction>> entry : interactions.entrySet()) {
            for (SelectInteraction selectInteraction : entry.getValue()) {
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

    static class InteractionComponents {
        private final SelectComponent selectComponent;
        private final SelectInteraction selectInteraction;

        public InteractionComponents(SelectComponent selectComponent, SelectInteraction selectInteraction) {
            this.selectComponent = selectComponent;
            this.selectInteraction = selectInteraction;
        }

        public void handle(StringSelectInteractionEvent event, String selectAuthorId, String invokerId) {
            // Check author
            if (!selectComponent.ignoreAuthor() && !selectAuthorId.equals(invokerId)) {
                return;
            }

            // Handle select
            selectComponent.handle(event);
            selectInteraction.updateInteraction();
        }
    }
}
