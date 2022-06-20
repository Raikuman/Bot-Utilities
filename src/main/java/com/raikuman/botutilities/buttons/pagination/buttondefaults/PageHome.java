package com.raikuman.botutilities.buttons.pagination.buttondefaults;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PageInterface;
import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;

import java.util.List;

/**
 * Handles going to the initial page of a selection menu
 *
 * @version 1.1 2022-19-06
 * @since 1.0
 */
public class PageHome implements ButtonInterface, PageInterface {

	private final String invoke;

	public PageHome(String invoke) {
		this.invoke = invoke;
	}

	@Override
	public void handle(ButtonContext ctx) {

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
}
