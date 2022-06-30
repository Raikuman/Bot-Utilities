package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageFirst;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageHome;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageLeft;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageRight;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * Created an object for pagination to provide buttons for an embed
 *
 * @version 1.1 2022-30-06
 * @since 1.0
 */
public class Pagination {

	private final String invoke, userId;
	private final boolean loop;
	private final int numPages;

	Pagination(String invoke, String userId, boolean loop, int numPages) {
		this.invoke = invoke;
		this.userId = userId;
		this.loop = loop;
		this.numPages = numPages;
	}

	/**
	 * Returns a button for moving left in a pagination
	 * @return The button for moving left
	 */
	public ItemComponent provideLeft() {
		PageLeft pageLeft = new PageLeft(invoke);

		Button button = Button.secondary(
			userId + ":" + pageLeft.getButtonId(),
			pageLeft.getEmoji()
		);

		if (!loop)
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
			userId + ":" + pageRight.getButtonId(),
			pageRight.getEmoji()
		);

		if (!loop && numPages == 1)
			return button.asDisabled();

		return button;
	}

	/**
	 * Returns a button for moving to the first page in a pagination
	 * @return Tbe button for the first page
	 */
	public ItemComponent provideFirst() {
		PageFirst pageFirst = new PageFirst(invoke);

		Button button = Button.secondary(
			userId + ":" + pageFirst.getButtonId(),
			pageFirst.getEmoji()
		);

		if (!loop && numPages == 1)
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
			userId + ":" + pageHome.getButtonId(),
			pageHome.getEmoji()
		);
	}
}
