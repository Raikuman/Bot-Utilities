package com.raikuman.botutilities.buttons.pagination.buttondefaults;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PageInterface;
import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles going to the page right of a pagination
 *
 * @version 1.1 2022-19-06
 * @since 1.0
 */
public class PageRight implements ButtonInterface, PageInterface {

	private final String invoke;

	public PageRight(String invoke) {
		this.invoke = invoke;
	}

	@Override
	public void handle(ButtonContext ctx) {
		MessageEditCallbackAction callbackAction = ctx.getCallbackAction();

		int currentPageNumber = PaginationResources.getCurrentPageNumber(ctx);
		if (currentPageNumber == -1)
			return;

		List<Button> buttonList = ctx.getButtons();
		int newPageNumber = currentPageNumber + 1;
		if (loopPagination()) {
			if (newPageNumber > getPages(ctx).size())
				newPageNumber = 1;

			if (newPageNumber == currentPageNumber) {
				callbackAction.queue();
				return;
			}
		} else {
			if (newPageNumber == getPages(ctx).size())
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
		return invoke + "pageright";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromMarkdown("➡️");
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
