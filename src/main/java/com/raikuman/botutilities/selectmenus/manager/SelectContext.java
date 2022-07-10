package com.raikuman.botutilities.selectmenus.manager;

import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

/**
 * Holds the context of the class SelectMenuInteractionEvent and breaks it down to commonly used methods
 * for select interfaces to access
 *
 * @version 1.0 2022-10-07
 * @since 1.1
 */
public class SelectContext implements EventContext {

	private final SelectMenuInteractionEvent event;

	public SelectContext(SelectMenuInteractionEvent event) {
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
	 * Return the full select event
	 * @return The select event
	 */
	public SelectMenuInteractionEvent getEvent() {
		return event;
	}
}
