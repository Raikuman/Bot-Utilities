package com.raikuman.botutilities.selectmenus.manager;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SelectMenuManager {

	private static final Logger logger = LoggerFactory.getLogger(SelectMenuManager.class);
	private final List<SelectInterface> selects = new ArrayList<>();

	public SelectMenuManager(List<SelectInterface> selects) {
		addSelects(selects);
	}

	public List<SelectInterface> getSelects() {
		return selects;
	}

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

	private void addSelects(List<SelectInterface> selects) {
		for (SelectInterface select : selects)
			addSelect(select);
	}

	public SelectInterface getSelect(String search) {
		for (SelectInterface select : selects)
			if (select.getMenuValue().equalsIgnoreCase(search))
				return select;

		return null;
	}

	public void handleEvent(SelectMenuInteractionEvent event, String menuValue) {
		SelectInterface select = getSelect(menuValue);
		if (select == null) {
			logger.info("Could not retrieve select from select manager: " + menuValue);
			return;
		}

		select.handle(new SelectContext(event));
	}
}
