package com.raikuman.botutilities.buttons.pagination.buttondefaults;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PageInterface;
import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.List;

/**
 * Handles going to the first page of a pagination
 *
 * @version 1.1 2022-19-06
 * @since 1.0
 */
public class PageFirst implements ButtonInterface, PageInterface {

	private final String invoke;

	public PageFirst(String invoke) {
		this.invoke = invoke;
	}

	@Override
	public void handle(ButtonContext ctx) {
		MessageEditCallbackAction callbackAction = ctx.getCallbackAction();

		int currentPageNumber = PaginationResources.getCurrentPageNumber(ctx);
		if (currentPageNumber == -1)
			return;

		if (currentPageNumber == 1) {
			callbackAction.queue();
			return;
		}

		callbackAction
			.setEmbeds(getPages(ctx).get(0).build())
			.queue();
	}

	@Override
	public String getButtonId() {
		return invoke + "pagefirst";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromUnicode("1️⃣");
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
