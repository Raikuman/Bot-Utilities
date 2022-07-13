package com.raikuman.botutilities.commands;

import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.commands.manager.CommandManager;
import com.raikuman.botutilities.configs.ConfigFileWriter;
import com.raikuman.botutilities.configs.Prefix;
import com.raikuman.botutilities.configs.defaults.DefaultConfig;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provides an event listener for commands for the JDA object
 *
 * @version 1.1 2022-13-07
 * @since 1.0
 */
public class CommandEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CommandEventListener.class);
	private final CommandManager manager;

	public CommandEventListener(List<CommandInterface> commandInterfaces) {
		this.manager = new CommandManager(commandInterfaces);

		ConfigFileWriter.writeConfigFiles(new DefaultConfig());
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + CommandEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.isFromGuild())
			return;

		User user = event.getAuthor();

		if (user.isBot() || event.isWebhookMessage())
			return;

		String prefix = Prefix.getPrefix(event.getGuild().getIdLong());
		if (prefix == null) {
			logger.error("Could not retrieve prefix");
			return;
		}

		String raw = event.getMessage().getContentRaw();

		if (raw.startsWith(prefix))
			manager.handleEvent(event);
	}
}
