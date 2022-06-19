package com.raikuman.botutilities.buttons.manager;

import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.List;

/**
 * Holds the context of the class ButtonInteractionEvent and breaks it down to commonly used methods for
 * button interfaces to access
 *
 * @version 1.2 2022-19-06
 * @since 1.0
 */
public class ButtonContext implements EventContext {

	private final ButtonInteractionEvent event;

	public ButtonContext(ButtonInteractionEvent event) {
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
	 * Return the full button event
	 * @return The message event
	 */
	public ButtonInteractionEvent getEvent() {
		return event;
	}

	/**
	 * Returns the edit action to edit the message
	 * @return The edit callback action
	 */
	public MessageEditCallbackAction getCallbackAction() {
		return event.deferEdit();
	}

	/**
	 * Returns a list of buttons from the button event
	 * @return The list of buttons
	 */
	public List<Button> getButtons() {
		return event.getMessage().getButtons();
	}
}
