package com.raikuman.botutilities.selectmenus.manager;

public interface SelectInterface {

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
