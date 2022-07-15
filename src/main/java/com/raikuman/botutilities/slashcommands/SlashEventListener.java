package com.raikuman.botutilities.slashcommands;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.configs.defaults.DefaultConfig;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.botutilities.slashcommands.manager.SlashManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provides an event listener for selects for the JDA object
 *
 * @version 1.0 2022-15-07
 * @since 1.2
 */
public class SlashEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SlashEventListener.class);
	private final SlashManager manager;

	public SlashEventListener(List<SlashInterface> slashInterfaces) {
		this.manager = new SlashManager(slashInterfaces);
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		loadSlashCommands(event.getJDA());

		logger.info("{}" + SlashEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (!event.isFromGuild())
			return;

		User user = event.getUser();

		if (user.isBot())
			return;

		manager.handleEvent(event);
	}

	/**
	 * Loads slash commands globally or through guilds, depending on config
	 * @param jda The jda object to update commands to
	 */
	private void loadSlashCommands(JDA jda) {
		String configValue = ConfigIO.readConfig(new DefaultConfig().fileName(), "globalappcommands");
		boolean loadGlobal = configValue != null && configValue.equalsIgnoreCase("true");

		if (loadGlobal) {
			jda.updateCommands().addCommands(manager.getSlashCommandData()).queue();
		} else {
			for (Guild guild : jda.getGuilds())
				guild.updateCommands().addCommands(manager.getSlashCommandData()).queue();
		}
	}
}
