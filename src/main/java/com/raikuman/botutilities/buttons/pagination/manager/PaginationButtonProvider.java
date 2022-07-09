package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageFirst;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageHome;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageLeft;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageRight;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A button provider for pagination to offer button interfaces to the button manager
 *
 * @version 2.0 2022-09-07
 * @since 1.1
 */
public class PaginationButtonProvider {

	private static final Logger logger = LoggerFactory.getLogger(PaginationButtonProvider.class);

	/**
	 * Provides button interfaces for a listener manager
	 * @param object The object to extract interface information from
	 * @return The list of button interfaces
	 */
	public static List<ButtonInterface> provideButtons(Object object) {
		String invoke;
		CommandInterface command = null;
		SelectInterface select = null;
		PageInvokeInterface pageinvokeInterface = null;

		if (object instanceof CommandInterface)
			command = (CommandInterface) object;

		if (object instanceof SelectInterface)
			select = (SelectInterface) object;

		if (object instanceof PageInvokeInterface)
			pageinvokeInterface = (PageInvokeInterface) object;

		if (command == null && select == null) {
			logger.error("No command or select found to provide pagination");
			return new ArrayList<>();
		}

		if (pageinvokeInterface == null) {
			logger.error("No page helper found to provide pagination");
			return new ArrayList<>();
		}

		invoke = getInvoke(command, select);

		if (invoke.isEmpty()) {
			logger.error("No invoke or menu value found to provide pagination");
			return new ArrayList<>();
		}

		return createButtons(invoke, pageinvokeInterface);
	}

	/**
	 * Creates button interfaces based on PageInvokeInterface methods
	 * @param invoke The invocation for the button id
	 * @param pageInvokeInterface The invocation interface to get page strings and items per page from
	 * @return A list of button interfaces
	 */
	private static List<ButtonInterface> createButtons(String invoke,
		PageInvokeInterface pageInvokeInterface) {
		List<ButtonInterface> buttonInterfaces = new ArrayList<>();
		buttonInterfaces.add(
			new PageLeft(invoke) {

				@Override
				public List<EmbedBuilder> getPages(EventContext ctx) {
					return PaginationResources.buildEmbeds(
						invoke,
						ctx.getEventMember().getEffectiveAvatarUrl(),
						pageInvokeInterface.pageStrings(ctx),
						pageInvokeInterface.itemsPerPage()
					);
				}

				@Override
				public boolean loopPagination() {
					return pageInvokeInterface.loopPagination();
				}
			}
		);

		buttonInterfaces.add(
			new PageRight(invoke) {

				@Override
				public List<EmbedBuilder> getPages(EventContext ctx) {
					return PaginationResources.buildEmbeds(
						invoke,
						ctx.getEventMember().getEffectiveAvatarUrl(),
						pageInvokeInterface.pageStrings(ctx),
						pageInvokeInterface.itemsPerPage()
					);
				}

				@Override
				public boolean loopPagination() {
					return pageInvokeInterface.loopPagination();
				}
			}
		);

		if (pageInvokeInterface.addHomeBtn())
			buttonInterfaces.add(
				1,
				new PageHome(invoke) {

					@Override
					public List<EmbedBuilder> getPages(EventContext ctx) {
						return PaginationResources.buildEmbeds(
							invoke,
							ctx.getEventMember().getEffectiveAvatarUrl(),
							pageInvokeInterface.pageStrings(ctx),
							pageInvokeInterface.itemsPerPage()
						);
					}

					@Override
					public boolean loopPagination() {
						return pageInvokeInterface.loopPagination();
					}

					@Override
					public List<EmbedBuilder> homePages(EventContext ctx) {
						return pageInvokeInterface.homePages(ctx);
					}

					@Override
					public List<ActionRow> homeActionRows(EventContext ctx) {
						return pageInvokeInterface.homeActionRows(ctx);
					}
				}
			);

		if (pageInvokeInterface.addFirstPageBtn())
			buttonInterfaces.add(
				1,
				new PageFirst(invoke) {

					@Override
					public List<EmbedBuilder> getPages(EventContext ctx) {
						return PaginationResources.buildEmbeds(
							invoke,
							ctx.getEventMember().getEffectiveAvatarUrl(),
							pageInvokeInterface.pageStrings(ctx),
							pageInvokeInterface.itemsPerPage()
						);
					}

					@Override
					public boolean loopPagination() {
						return pageInvokeInterface.loopPagination();
					}
				}
			);

		return buttonInterfaces;
	}

	/**
	 * Returns the invocation string given the command or select interface object
	 * @param commandInterface The command interface to check for an invocation string
	 * @param selectInterface The select interface to check for an invocation string
	 * @return The invocation string
	 */
	private static String getInvoke(CommandInterface commandInterface, SelectInterface selectInterface) {
		if (commandInterface != null) {
			if (commandInterface.getInvoke().isEmpty()) {
				logger.error("No invoke found to provide pagination");
				return "";
			}

			return commandInterface.getInvoke();
		} else if (selectInterface != null) {
			if (selectInterface.getMenuValue().isEmpty()) {
				logger.error("No menu value found to provide pagination");
				return "";
			}

			return selectInterface.getMenuValue();
		} else {
			return "";
		}
	}
}
