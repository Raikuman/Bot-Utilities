package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.invocation.manager.SlashManager;
import com.raikuman.botutilities.invocation.type.Slash;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SlashEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SlashEventListener.class);
    private final SlashManager manager;

    public SlashEventListener(List<Slash> slashes) {
        manager = new SlashManager(slashes);
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + SlashEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        manager.handleEvent(event);
    }
}
