package com.raikuman.botutilities.buttons.manager;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages handling button events to invoke commands. Buttons are added and checked if there are multiple
 * buttons with the same button id being added.
 *
 * @version 1.0 2022-18-06
 * @since 1.0
 */
public class ButtonManager {

	private static final Logger logger = LoggerFactory.getLogger(ButtonManager.class);
	private final List<ButtonInterface> buttons = new ArrayList<>();

	public ButtonManager(List<ButtonInterface> buttons) {
		addButtons(buttons);
	}

	/**
	 * Gets the list of button interfaces
	 * @return The list of button interfaces
	 */
	public List<ButtonInterface> getButtons() {
		return buttons;
	}

	/**
	 * Adds a button interface to the button list
	 * @param button The button to add to the button list
	 */
	private void addButton(ButtonInterface button) {
		boolean buttonFound = buttons.stream().anyMatch(
			found -> found.getButtonId().equalsIgnoreCase(button.getButtonId())
		);

		if (buttonFound) {
			logger.error("A button with this id already exists: " + button.getButtonId());
			return;
		}

		buttons.add(button);
	}

	/**
	 * Adds multiple button interfaces to the button list
	 * @param buttons The list of buttons to add to the button list
	 */
	private void addButtons(List<ButtonInterface> buttons) {
		for (ButtonInterface button : buttons)
			addButton(button);
	}

	/**
	 * Gets a button from the button list using a string to search for the button id of the button
	 * @param search The button id string to search for a button
	 * @return The found button, else null
	 */
	public ButtonInterface getButton(String search) {
		for (ButtonInterface button : buttons)
			if (button.getButtonId().equalsIgnoreCase(search))
				return button;

		return null;
	}

	/**
	 * Handles the button event and checks if a button exists with the given button id from the event. It
	 * will then send a button context to handle the button if it exists
	 * @param event The event to construct a button context
	 * @param buttonId The button id to check if a button exists in the manager
	 */
	public void handleEvent(ButtonInteractionEvent event, String buttonId) {
		ButtonInterface button = getButton(buttonId);
		if (button == null) {
			logger.info("Could not retrieve button from button manager: " + buttonId);
			return;
		}

		button.handle(new ButtonContext(event));
	}
}
