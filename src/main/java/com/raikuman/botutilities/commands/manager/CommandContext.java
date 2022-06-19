package com.raikuman.botutilities.commands.manager;

import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Holds the context of the class MessageReceivedEvent and breaks it down to commonly used methods for
 * command interfaces to access
 *
 * @version 1.1 2022-19-06
 * @since 1.0
 */
public class CommandContext implements EventContext {

	private final MessageReceivedEvent event;
	private final List<String> args;

	public CommandContext(MessageReceivedEvent event, List<String> args) {
		this.event = event;
		this.args = args;
	}

	@Override
	public Member getEventMember() {
		return event.getMember();
	}

	@Override
	public Guild getGuild() {
		return event.getGuild();
	}

	/**
	 * Return the full message event
	 * @return The message event
	 */
	public MessageReceivedEvent getEvent() {
		return event;
	}

	/**
	 * Return the list of args from the event
	 * @return The list of args as strings
	 */
	public List<String> getArgs() {
		return args;
	}

	/**
	 * Return the text channel where the event came from
	 * @return The event text channel
	 */
	public TextChannel getChannel() {
		return event.getTextChannel();
	}
}
