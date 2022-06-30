package com.raikuman.botutilities.selectmenus.manager;

import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

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

	public SelectMenuInteractionEvent getEvent() {
		return event;
	}
}
