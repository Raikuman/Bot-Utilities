package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

/**
 * Provides an interface for pagination buttons
 *
 * @version 1.0 2022-19-06
 * @since 1.0
 */
public interface PageInterface {

	/**
	 * Returns the list of EmbedBuilders to act as pages for the button
	 * @param ctx The button context to use to set embeds
	 * @return The list of embed builders
	 */
	List<EmbedBuilder> getPages(ButtonContext ctx);

	/**
	 * Returns a boolean on whether to loop around a pagination
	 * @return The boolean to loop a pagination
	 */
	boolean loopPagination();
}
