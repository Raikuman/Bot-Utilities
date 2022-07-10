package com.raikuman.botutilities.selectmenus.manager;

/**
 * Provides interface for creating selects
 *
 * @version 1.0 2022-10-07
 * @since 1.1
 */
public interface SelectInterface {

	/**
	 * Handles what the select will do using select context
	 * @param ctx The select context for handling
	 */
	void handle(SelectContext ctx);

	/**
	 * Returns the button id that the button manager will check for a button
	 * @return The button id string
	 */
	String getMenuValue();

	/**
	 * Returns the label that the button will display
	 * @return The label string
	 */
	String getLabel();
}
