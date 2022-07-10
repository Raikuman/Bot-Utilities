package com.raikuman.botutilities.selectmenus;

import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.botutilities.selectmenus.manager.SelectMenuManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class SelectEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SelectEventListener.class);
	private final SelectMenuManager manager;

	public SelectEventListener(List<SelectInterface> selectInterfaces) {
		this.manager = new SelectMenuManager(selectInterfaces);
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + SelectEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {
		if (!event.isFromGuild())
			return;

		User user = event.getUser();

		if (user.isBot())
			return;

		String value = getMenuValue(event.getValues());
		if (value == null)
			return;

		// Split menu value
		String[] id = value.split(":");
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

		manager.handleEvent(event, type);
	}

	private String getMenuValue(List<String> values) {

		if (values.size() == 1)
			return values.get(0);
		else
			return null;
	}
}
