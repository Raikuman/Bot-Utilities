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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A button provider for pagination to offer button interfaces to the button manager
 *
 * @version 1.5 2022-30-06
 * @since 1.0
 */
public class PaginationButtonProvider {

	private static final Logger logger = LoggerFactory.getLogger(PaginationButtonProvider.class);
	private final CommandInterface command;
	private final SelectInterface select;
	private final PageInvokeInterface pageCommandInterface;
	private String invoke;

	public PaginationButtonProvider(Object object) {
		invoke = "";

		if (object instanceof CommandInterface)
			command = (CommandInterface) object;
		else
			command = null;

		if (object instanceof SelectInterface)
			select = (SelectInterface) object;
		else
			select = null;

		if (object instanceof PageInvokeInterface)
			pageCommandInterface = (PageInvokeInterface) object;
		else
			pageCommandInterface = null;
	}

	/**
	 * Provides button interfaces for a listener
	 * @return The button interface list
	 */
	public List<ButtonInterface> provideButtons() {
		if (command == null && select == null) {
			logger.error("No command or select found to provide pagination");
			return new ArrayList<>();
		}

		if (pageCommandInterface == null) {
			logger.error("No page helper found to provide pagination");
			return new ArrayList<>();
		}

		if (command != null) {
			if (command.getInvoke().isEmpty()) {
				logger.error("No invoke found to provide pagination");
				return new ArrayList<>();
			}

			invoke = command.getInvoke();
		}

		if (select != null) {
			if (select.getMenuValue().isEmpty()) {
				logger.error("No menu value found to provide pagination");
				return new ArrayList<>();
			}

			invoke = select.getMenuValue();
		}

		if (invoke.isEmpty()) {
			logger.error("No invoke or menu value found to provide pagination");
			return new ArrayList<>();
		}

		List<ButtonInterface> buttonInterfaces = new ArrayList<>();
		buttonInterfaces.add(
			new PageLeft(invoke) {

				@Override
				public List<EmbedBuilder> getPages(EventContext ctx) {
					return PaginationResources.buildEmbeds(
						invoke,
						ctx.getEventMember().getEffectiveAvatarUrl(),
						pageCommandInterface.pageStrings(ctx),
						pageCommandInterface.itemsPerPage()
					);
				}

				@Override
				public boolean loopPagination() {
					return pageCommandInterface.loopPagination();
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
						pageCommandInterface.pageStrings(ctx),
						pageCommandInterface.itemsPerPage()
					);
				}

				@Override
				public boolean loopPagination() {
					return pageCommandInterface.loopPagination();
				}
			}
		);

		if (pageCommandInterface.addHomeBtn())
			buttonInterfaces.add(
				1,
				new PageHome(invoke) {

					@Override
					public List<EmbedBuilder> getPages(EventContext ctx) {
						return PaginationResources.buildEmbeds(
							invoke,
							ctx.getEventMember().getEffectiveAvatarUrl(),
							pageCommandInterface.pageStrings(ctx),
							pageCommandInterface.itemsPerPage()
						);
					}

					@Override
					public boolean loopPagination() {
						return pageCommandInterface.loopPagination();
					}
				}
			);

		if (pageCommandInterface.addFirstPageBtn())
			buttonInterfaces.add(
				1,
				new PageFirst(invoke) {

					@Override
					public List<EmbedBuilder> getPages(EventContext ctx) {
						return PaginationResources.buildEmbeds(
							invoke,
							ctx.getEventMember().getEffectiveAvatarUrl(),
							pageCommandInterface.pageStrings(ctx),
							pageCommandInterface.itemsPerPage()
						);
					}

					@Override
					public boolean loopPagination() {
						return pageCommandInterface.loopPagination();
					}
				}
			);

		return buttonInterfaces;
	}
}
