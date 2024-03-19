package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public abstract class ButtonComponent {

    public ComponentHandler componentHandler;
    public abstract void handle(ButtonInteractionEvent ctx);
    public abstract String getInvoke();
    public Emoji displayEmoji() { return null; }
    public abstract String displayLabel();
    public ButtonStyle buttonStyle() { return ButtonStyle.PRIMARY; }

    public boolean isDisabled() {
        return false;
    }
    public boolean ignoreAuthor() {
        return false;
    }
}
