package com.raikuman.botutilities.invocation.component;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {

    public static ActionRow buildButtons(User user, List<ButtonComponent> buttonComponents) {
        // Create components
        List<ItemComponent> components = new ArrayList<>();
        for (ButtonComponent buttonComponent : buttonComponents) {
            Emoji emoji = buttonComponent.displayEmoji();
            String label = buttonComponent.displayLabel();
            ButtonStyle style = buttonComponent.buttonStyle();
            String buttonId = user.getId() + ":" + buttonComponent.getInvoke();

            // Check for label, replace with 0-width space if null
            if (label == null) label = "\u200B";

            // Check for style
            if (style == null) style = ButtonStyle.SECONDARY;

            if (emoji == null) {
                components.add(Button.of(style, buttonId, label));
            } else {
                components.add(Button.of(style, buttonId, label, emoji));
            }
        }

        return ActionRow.of(components);
    }
}
