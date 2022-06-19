package com.raikuman.botutilities.context;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/**
 * Provides an interface for different contexts
 *
 * @version 1.0 2022-19-06
 * @since 1.1
 */
public interface EventContext {

	/**
	 * Return the member who created the event
	 * @return The event member
	 */
	Member getEventMember();

	/**
	 * Return the guild where the event came from
	 * @return The event guild
	 */
	Guild getGuild();
}
