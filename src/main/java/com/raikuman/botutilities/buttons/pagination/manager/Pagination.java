package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageFirst;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageHome;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageLeft;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageRight;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

/**
 * Created an object for pagination to provide buttons for an embed
 *
 * @version 2.2 2022-10-07
 * @since 1.0
 */
public class Pagination {

	private final Member member;
	private final String invoke, pageName;
	private final List<String> paginationStrings;
	private final boolean loop;
	private final int itemsPerPage, numPages;

	public Pagination(Member member, String invoke, String pageName, List<String> paginationStrings,
		int itemsPerPage,
		boolean loop) {
		this.member = member;
		this.invoke = invoke;
		this.pageName = pageName;
		this.paginationStrings = paginationStrings;
		this.loop = loop;
		this.itemsPerPage = itemsPerPage;

		this.numPages = (int) Math.ceil(this.paginationStrings.size() / (double) this.itemsPerPage);
	}

	/**
	 * Returns a button for moving left in a pagination
	 * @return The button for moving left
	 */
	public ItemComponent provideLeft() {
		PageLeft pageLeft = new PageLeft(invoke);

		Button button = Button.secondary(
			member.getId() + ":" + pageLeft.getButtonId(),
			pageLeft.getEmoji()
		);

		if (!loop)
			return button.asDisabled();

		if (numPages == 1)
			return button.asDisabled();

		return button;
	}

	/**
	 * Returns a button for moving right in a pagination
	 * @return The button for moving right
	 */
	public ItemComponent provideRight() {
		PageRight pageRight = new PageRight(invoke);

		Button button = Button.secondary(
			member.getId() + ":" + pageRight.getButtonId(),
			pageRight.getEmoji()
		);

		if (!loop && numPages == 1)
			return button.asDisabled();

		if (numPages == 1)
			return button.asDisabled();

		return button;
	}

	/**
	 * Returns a button for moving to the first page in a pagination
	 * @return The button for the first page
	 */
	public ItemComponent provideFirst() {
		PageFirst pageFirst = new PageFirst(invoke);

		Button button = Button.secondary(
			member.getId() + ":" + pageFirst.getButtonId(),
			pageFirst.getEmoji()
		);

		if (!loop && numPages == 1)
			return button.asDisabled();

		if (numPages == 1)
			return button.asDisabled();

		return button;
	}

	/**
	 * Returns a button for moving to a previous selection menu
	 * @return The button for going back to a selection menu
	 */
	public ItemComponent provideHome() {
		PageHome pageHome = new PageHome(invoke);
		return Button.secondary(
			member.getId() + ":" + pageHome.getButtonId(),
			pageHome.getEmoji()
		);
	}

	/**
	 * Returns an EmbedBuilder list to provide embeds
	 * @return The list of EmbedBuilders
	 */
	public List<EmbedBuilder> buildEmbeds() {
		return PaginationResources.buildEmbeds(
			pageName,
			member.getEffectiveAvatarUrl(),
			paginationStrings,
			itemsPerPage
		);
	}
}
