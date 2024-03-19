package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

public abstract class ModalComponent {

    public ComponentHandler componentHandler;
    public abstract void handle(ModalInteractionEvent ctx);
    public abstract String getInvoke();
    public abstract Modal getModal();
}
