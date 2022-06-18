package com.raikuman.botutilities.buttons;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.manager.ButtonManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Provides an event listener for buttons for the JDA object
 *
 * @version 1.0 2022-18-06
 * @since 1.0
 */
public class ButtonEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ButtonEventListener.class);
	private final ButtonManager manager;

	public ButtonEventListener(List<ButtonInterface> buttonInterfaces) {
		this.manager = new ButtonManager(buttonInterfaces);
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + ButtonEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
		if (!event.isFromGuild())
			return;

		User user = event.getUser();

		if (user.isBot())
			return;

		// Split component id
		String[] id = event.getComponentId().split(":");
		if (id.length != 2) {
			logger.error("Could not retrieve component id");
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
}
