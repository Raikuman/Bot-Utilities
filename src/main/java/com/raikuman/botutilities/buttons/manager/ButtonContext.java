package com.raikuman.botutilities.buttons.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.List;

/**
 * Holds the context of the class ButtonInteractionEvent and breaks it down to commonly used methods for
 * button interfaces to access
 *
 * @version 1.1 2022-18-06
 * @since 1.0
 */
public class ButtonContext {

	private final ButtonInteractionEvent event;

	public ButtonContext(ButtonInteractionEvent event) {
		this.event = event;
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
		return this.event.getMessage().getButtons();
	}

	/**
	 * Returns the member who started the event
	 * @return The member of the event
	 */
	public Member getEventMember() {
		return this.event.getMember();
	}
}
