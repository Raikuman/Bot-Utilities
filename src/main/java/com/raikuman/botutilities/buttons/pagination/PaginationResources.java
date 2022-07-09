package com.raikuman.botutilities.buttons.pagination;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.helpers.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A resource class for making pagination easier
 *
 * @version 1.1 2022-09-07
 * @since 1.0
 */
public class PaginationResources {

	private static final Logger logger = LoggerFactory.getLogger(PaginationResources.class);

	/**
	 * Gets the current page number of a given page embed
	 * @param ctx The button context to get embed
	 * @return The current page number
	 */
	public static int getCurrentPageNumber(ButtonContext ctx) {
		List<MessageEmbed> buttonEmbeds = ctx.getEvent().getMessage().getEmbeds();
		if (buttonEmbeds.size() < 1) {
			logger.error("No embeds can be found on this message");
			ctx.getCallbackAction().queue();
			return -1;
		}

		MessageEmbed.Footer footer = buttonEmbeds.get(0).getFooter();
		if (footer == null) {
			logger.error("Could not get footer from embed");
			ctx.getCallbackAction().queue();
			return -1;
		}

		if (footer.getText() == null) {
			logger.error("No page number could be found from the embed's footer");
			ctx.getCallbackAction().queue();
			return -1;
		}

		String trimmedPage = footer.getText().replace("Page ", "");
		String [] pageNumbers = trimmedPage.split("/");
		if (pageNumbers.length != 2) {
			logger.error("Failed to trim page numbers from the embed's footer");
			ctx.getCallbackAction().queue();
			return -1;
		}

		try {
			return Integer.parseInt(pageNumbers[0]);
		} catch (NumberFormatException e) {
			logger.error("Failed to retrieve number from page number");
			return -1;
		}
	}

	/**
	 * Builds a list of embed builders given information for the embeds
	 * @param pageName The name of the pagination
	 * @param avatarUrl The user's avatar url
	 * @param stringList The list of strings to write pages for pagination
	 * @param itemsPerPage The number of items (strings) to write per page
	 * @return The list of embed builders
	 */
	public static List<EmbedBuilder> buildEmbeds(String pageName, String avatarUrl, List<String> stringList,
		int itemsPerPage) {
		int numPages = (int) Math.ceil(stringList.size() / (double) itemsPerPage);

		List<EmbedBuilder> embedBuilderList = new ArrayList<>();
		EmbedBuilder builder = new EmbedBuilder();
		StringBuilder descriptionBuilder;

		int stringCount = 0;
		for (int i = 0; i < numPages; i++) {
			builder
				.setAuthor(pageName, null, avatarUrl)
				.setColor(RandomColor.getRandomColor())
				.setFooter("Page " + (i + 1) + "/" + numPages);

			descriptionBuilder = builder.getDescriptionBuilder();

			for (int j = 0; j < itemsPerPage; j++) {
				if (stringCount < stringList.size())
					descriptionBuilder
						.append(stringList.get(stringCount))
						.append("\n\n");

				stringCount++;
			}

			embedBuilderList.add(builder);
			builder = new EmbedBuilder();
		}

		return embedBuilderList;
	}

	/**
	 * Enables all buttons in a list of buttons
	 * @param buttons The list of enabled buttons
	 */
	public static void enableButtons(List<Button> buttons) {
		for (Button button : buttons)
			buttons.set(buttons.indexOf(button), button.asEnabled());
	}
}
