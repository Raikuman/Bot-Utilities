package com.raikuman.botutilities.invocation.type;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface SelectComponent {

    void handle(StringSelectInteractionEvent ctx);
    String getInvoke();
    String displayLabel();

    default boolean ignoreAuthor() { return false; }
}
