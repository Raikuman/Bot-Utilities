package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public interface Slash {

    void handle(SlashCommandInteractionEvent ctx);
    String getInvoke();
    String getDescription();
    default CommandData getCommandData() {
        return Commands.slash(getInvoke(), getDescription());
    }
    default Category getCategory() { return null; }
}
