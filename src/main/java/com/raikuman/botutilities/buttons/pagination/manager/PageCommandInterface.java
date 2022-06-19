package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.context.EventContext;

import java.util.List;

/**
 * Provides an interface for commands that use pagination
 *
 * @version 1.1 2022-19-06
 * @since 1.0
 */
public interface PageCommandInterface {

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
}
