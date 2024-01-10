package com.raikuman.botutilities.invocation.listener;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.defaults.DefaultConfig;
import com.raikuman.botutilities.invocation.manager.SlashManager;
import com.raikuman.botutilities.invocation.type.Slash;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
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

        loadSlashCommands(event.getJDA());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        manager.handleEvent(event);
    }

    private void loadSlashCommands(JDA jda) {
        boolean loadGlobal = Boolean.parseBoolean(new ConfigData(new DefaultConfig()).getConfig("globalapp"));

        if (loadGlobal) {
            jda.updateCommands().addCommands(manager.getSlashCommandData()).queue();

            for (Guild guild : jda.getGuilds()) {
                guild.updateCommands().queue();
            }
        } else {
            loadCommandsLocal(jda);
        }
    }

    private void loadCommandsLocal(JDA jda) {
        jda.updateCommands().queue();

        for (Guild guild : jda.getGuilds()) {
            guild.updateCommands().addCommands(manager.getSlashCommandData()).queue();
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        loadCommandsLocal(event.getJDA());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        loadCommandsLocal(event.getJDA());
    }
}
