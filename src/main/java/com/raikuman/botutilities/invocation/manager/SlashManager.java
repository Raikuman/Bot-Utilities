package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.invocation.type.Slash;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlashManager {

    private static final Logger logger = LoggerFactory.getLogger(SlashManager.class);
    private final HashMap<String, Slash> slashes;

    public SlashManager(List<Slash> slashes) {
        HashMap<String, Slash> slashMap = new HashMap<>();
        for (Slash slash : slashes) {
            slashMap.put(slash.getInvoke(), slash);
        }

        this.slashes = slashMap;
    }

    public List<CommandData> getSlashCommandData() {
        List<CommandData> commandData = new ArrayList<>();
        for (Map.Entry<String, Slash> slash : slashes.entrySet()) {
            CommandData data = slash.getValue().getCommandData();
            if (data == null) {
                continue;
            }

            commandData.add(slash.getValue().getCommandData());
        }

        return commandData;
    }

    public void handleEvent(SlashCommandInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Retrieve slash
        Slash slash = slashes.get(event.getName());
        if (slash == null) {
            logger.error("Invalid slash invocation: " + event.getName());
            return;
        }

        // Handle slash
        slash.handle(event);
    }
}
