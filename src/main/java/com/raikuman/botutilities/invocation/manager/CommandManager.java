package com.raikuman.botutilities.invocation.manager;

import com.raikuman.botutilities.defaults.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final HashMap<List<String>, Command> commands;

    public CommandManager(List<Command> commands) {
        // Process commands to map
        HashMap<List<String>, Command> commandMap = new HashMap<>();
        for (Command command : commands) {
            List<String> aliases = new ArrayList<>(command.getAliases());
            aliases.add(command.getInvoke());

            aliases = aliases.stream().map(String::toLowerCase).collect(Collectors.toList());
            commandMap.put(aliases, command);
        }

        this.commands = commandMap;
    }

    public Command getCommand(String commandInvoke) {
        for (Map.Entry<List<String>, Command> command : commands.entrySet()) {
            if (command.getKey().contains(commandInvoke)) {
                return command.getValue();
            }
        }

        return null;
    }

    public void handleEvent(MessageReceivedEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Check user
        User user = event.getAuthor();
        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        // Retrieve prefix
        String prefix = DefaultDatabaseHandler.getPrefix(event.getGuild());
        if (prefix.isEmpty()) {
            logger.error("Could not retrieve prefix in command handler for guild: " + event.getGuild().getName());
            return;
        }

        // Split message content
        String[] split = event.getMessage().getContentRaw()
            .replaceFirst(
                "(?i)" + Pattern.quote(prefix),
                "")
            .split("\\s+");

        // Retrieve command from handler
        Command command = getCommand(split[0].toLowerCase());
        if (command == null) {
            logger.error("Could not retrieve command from command handler with invoke: " + split[0].toLowerCase());
            return;
        }

        // Get args from message for command
        List<String> args = Arrays.asList(split).subList(1, split.length);

        // Handle command
        command.handle(new CommandContext(event, args));
    }
}