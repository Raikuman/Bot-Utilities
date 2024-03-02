package com.raikuman.botutilities.invocation.component;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

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

            Button button;
            if (emoji == null) {
                button = Button.of(style, buttonId, label);
            } else {
                button = Button.of(style, buttonId, label, emoji);
            }

            if (buttonComponent.isDisabled()) {
                button = button.asDisabled();
            }

            components.add(button);
        }

        return ActionRow.of(components);
    }

    public static ActionRow buildStringSelectMenu(String invoke, String placeholder, User user,
                                                  List<SelectComponent> selectComponents) {
        // Create components
        StringSelectMenu.Builder menu = StringSelectMenu.create(user.getId() + ":" + invoke)
            .setPlaceholder(placeholder)
            .setRequiredRange(1, 1);

        for (SelectComponent selectComponent : selectComponents) {
            menu.addOptions(SelectOption.of(selectComponent.displayLabel(), selectComponent.getInvoke()));
        }

        return ActionRow.of(menu.build());
    }
}
