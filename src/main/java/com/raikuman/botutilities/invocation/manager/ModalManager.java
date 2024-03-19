package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.invocation.type.ModalComponent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ModalManager {

    private static final Logger logger = LoggerFactory.getLogger(ModalManager.class);
    private final HashMap<String, ModalComponent> modals = new HashMap<>();

    public void addModal(ModalComponent modalComponent) {
        modals.putIfAbsent(modalComponent.getInvoke(), modalComponent);
    }

    public void handleEvent(ModalInteractionEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        // Retrieve modal
        ModalComponent modalComponent = modals.get(event.getModalId());
        if (modalComponent == null) {
            return;
        }

        modalComponent.handle(event);
    }
}
