package com.raikuman.botutilities.slashcommands.manager;

import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Holds the context of the class SlashCommandInteractionEvent and breaks it down to commonly used methods for
 * slash interfaces to access
 *
 * @version 1.0 2022-15-07
 * @since 1.2
 */
public class SlashContext implements EventContext {

	private final SlashCommandInteractionEvent event;

	public SlashContext(SlashCommandInteractionEvent event) {
		this.event = event;
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
	 * Return the full slash event
	 * @return The message event
	 */
	public SlashCommandInteractionEvent getEvent() {
		return event;
	}

	/**
	 * Return the text channel where the event came from
	 * @return The event text channel
	 */
	public TextChannel getChannel() {
		return event.getTextChannel();
	}
}
