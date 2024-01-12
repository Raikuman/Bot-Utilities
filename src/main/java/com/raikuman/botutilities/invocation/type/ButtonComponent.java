package com.raikuman.botutilities.invocation.type;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public interface ButtonComponent {

    void handle(ButtonInteractionEvent ctx);
    String getInvoke();
    Emoji displayEmoji();
    String displayLabel();
    ButtonStyle buttonStyle();
}
