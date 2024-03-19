package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public abstract class SelectComponent {

    public ComponentHandler componentHandler;
    public abstract void handle(StringSelectInteractionEvent ctx);
    public abstract String getInvoke();
    public abstract String displayLabel();

    public boolean ignoreAuthor() { return false; }
}
