package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public abstract class Slash {

    public ComponentHandler componentHandler;
    public abstract void handle(SlashCommandInteractionEvent ctx);
    public abstract String getInvoke();
    public String getDescription() {
        return "";
    }
    public SlashCommandData getCommandData() {
        if (getInvoke() == null || getDescription() == null) {
            return null;
        }

        return Commands.slash(getInvoke(), getDescription());
    }
    public List<Category> getCategories() { return null; }
}
