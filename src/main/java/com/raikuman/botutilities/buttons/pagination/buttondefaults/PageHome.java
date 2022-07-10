package com.raikuman.botutilities.buttons.pagination.buttondefaults;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PageButtonInterface;
import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handles going to the initial page of a selection menu
 *
 * @version 1.2 2022-30-06
 * @since 1.0
 */
public class PageHome implements ButtonInterface, PageButtonInterface {

	private static final Logger logger = LoggerFactory.getLogger(PageHome.class);

	private final String invoke;

	public PageHome(String invoke) {
		this.invoke = invoke;
	}

	@Override
	public void handle(ButtonContext ctx) {
		MessageEditCallbackAction callbackAction = ctx.getCallbackAction();

		if (homePages(ctx) == null)
			return;

		if (homeActionRows(ctx) == null) {
			logger.warn("No action row provided, continuing...");
			callbackAction
				.setEmbeds(homePages(ctx).get(0).build())
				.queue();
		} else {
			callbackAction
				.setEmbeds(homePages(ctx).get(0).build())
				.setActionRows(homeActionRows(ctx))
				.queue();
		}
	}

	@Override
	public String getButtonId() {
		return invoke + "pagehome";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromMarkdown("\uD83C\uDFE0");
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public List<EmbedBuilder> getPages(EventContext ctx) {
		return null;
	}

	@Override
	public boolean loopPagination() {
		return false;
	}

	public List<ActionRow> homeActionRows(EventContext ctx) {
		return null;
	}

	public List<EmbedBuilder> homePages(EventContext ctx) {
		return null;
	}
}
