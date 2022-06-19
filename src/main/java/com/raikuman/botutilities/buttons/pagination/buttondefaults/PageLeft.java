package com.raikuman.botutilities.buttons.pagination.buttondefaults;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PageInterface;
import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles going to the page left of a pagination
 *
 * @version 1.0 2022-19-06
 * @since 1.0
 */
public class PageLeft implements ButtonInterface, PageInterface {

	private final String invoke;

	public PageLeft(String invoke) {
		this.invoke = invoke;
	}

	@Override
	public void handle(ButtonContext ctx) {
		MessageEditCallbackAction callbackAction = ctx.getCallbackAction();

		int currentPageNumber = PaginationResources.getCurrentPageNumber(ctx);
		if (currentPageNumber == -1)
			return;

		List<Button> buttonList = ctx.getButtons();
		int newPageNumber = currentPageNumber - 1;
		if (loopPagination()) {
			if (newPageNumber < 1)
				newPageNumber = getPages(ctx).size();
		} else {
			if (newPageNumber == 1)
				buttonList
					.set(buttonList.indexOf(ctx.getEvent().getButton()), ctx.getEvent().getButton().asDisabled());
			else
				PaginationResources.enableButtons(buttonList);
		}

		List<ActionRow> actionRows = new ArrayList<>();
		actionRows.add(ActionRow.of(buttonList));

		callbackAction
			.setEmbeds(getPages(ctx).get(newPageNumber - 1).build())
			.setActionRows(actionRows)
			.queue();
	}

	@Override
	public String getButtonId() {
		return invoke + "pageleft";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromMarkdown("⬅️");
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public List<EmbedBuilder> getPages(ButtonContext ctx) {
		return null;
	}

	@Override
	public boolean loopPagination() {
		return false;
	}
}
