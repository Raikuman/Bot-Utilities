package com.raikuman.botutilities.selectmenus.manager;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages handling select events to invoke selects. Selects are added and checked if there are multiple
 * selects with the same invoke being added.
 *
 * @version 1.0 2022-10-07
 * @since 1.1
 */
public class SelectMenuManager {

	private static final Logger logger = LoggerFactory.getLogger(SelectMenuManager.class);
	private final List<SelectInterface> selects = new ArrayList<>();

	public SelectMenuManager(List<SelectInterface> selects) {
		addSelects(selects);
	}

	/**
	 * Gets the list of select interfaces
	 * @return The list of select interfaces
	 */
	public List<SelectInterface> getSelects() {
		return selects;
	}

	/**
	 * Adds a select interface to the select list
	 * @param select The select to add to the select list
	 */
	private void addSelect(SelectInterface select) {
		boolean selectFound = selects.stream().anyMatch(
			found -> found.getMenuValue().equals(select.getMenuValue())
		);

		if (selectFound) {
			logger.error("A select with this menu value already exists: " + select.getMenuValue());
			return;
		}

		selects.add(select);
	}

	/**
	 * Adds multiple select interfaces to the select list
	 * @param selects The list of selects to add to the select list
	 */
	private void addSelects(List<SelectInterface> selects) {
		for (SelectInterface select : selects)
			addSelect(select);
	}

	/**
	 * Gets a select from the select list using a string to search for the menu value of the select
	 * @param search The component id to search for a select
	 * @return The found select, else null
	 */
	public SelectInterface getSelect(String search) {
		for (SelectInterface select : selects)
			if (select.getMenuValue().equalsIgnoreCase(search))
				return select;

		return null;
	}

	public void handleEvent(SelectMenuInteractionEvent event) {
		String menuValue;
		if (event.getValues().size() == 1) {
			menuValue = event.getValues().get(0);
		} else {
			logger.error("Could not retrieve menu value");
			return;
		}

		// Split menu value
		String[] id = menuValue.split(":");
		if (id.length != 2) {
			logger.error("Could not retrieve menu value");
			return;
		}

		String authorId = id[0];
		String type = id[1];

		if (!authorId.equals(event.getUser().getId())) {
			event.deferEdit().queue();
			return;
		}

		SelectInterface select = getSelect(type);
		if (select == null) {
			logger.info("Could not retrieve select from select manager: " + menuValue);
			return;
		}

		select.handle(new SelectContext(event));
	}
}
