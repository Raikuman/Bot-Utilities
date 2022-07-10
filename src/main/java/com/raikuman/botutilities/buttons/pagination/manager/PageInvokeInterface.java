package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;

/**
 * Provides an interface for commands that use pagination
 *
 * @version 1.3 2022-10-07
 * @since 1.0
 */
public interface PageInvokeInterface {

	/**
	 * Returns the page name of the pagination
	 * @return The page name
	 */
	String pageName();

	/**
	 * Returns the list of strings that make up a page
	 * @return The list of strings
	 */
	List<String> pageStrings(EventContext ctx);

	/**
	 * Returns the number of items that a page should have
	 * @return The items per page
	 */
	int itemsPerPage();

	/**
	 * Returns a boolean on whether to loop around a pagination
	 * @return The boolean to loop a pagination
	 */
	boolean loopPagination();

	/**
	 * Returns a boolean on whether to add a home button to the pagination
	 * @return The boolean to add a home button
	 */
	boolean addHomeBtn();

	/**
	 * Returns a boolean on whether to add a first page button to the pagination
	 * @return The boolean to add a first page button
	 */
	boolean addFirstPageBtn();

	/**
	 * Returns the action rows of the original (home) message
	 * @param ctx The event context for handling
	 * @return The list of action rows
	 */
	default List<ActionRow> homeActionRows(EventContext ctx) {
		return null;
	}

	/**
	 * Returns the embed builders of the original (home) message
	 * @param ctx The event context for handling
	 * @return The list of embed builders
	 */
	default List<EmbedBuilder> homePages(EventContext ctx) {
		return null;
	}
}
