package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageFirst;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageHome;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageLeft;
import com.raikuman.botutilities.buttons.pagination.buttondefaults.PageRight;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A button provider for pagination to offer button interfaces to the button manager
 *
 * @version 1.0 2022-19-06
 * @since 1.0
 */
public class PaginationButtonProvider {

	private static final Logger logger = LoggerFactory.getLogger(PaginationButtonProvider.class);
	private final CommandInterface command;
	private final PageCommandInterface pageCommandInterface;

	public PaginationButtonProvider(Object object) {
		if (object instanceof CommandInterface)
			command = (CommandInterface) object;
		else
			command = null;

		if (object instanceof PageCommandInterface)
			pageCommandInterface = (PageCommandInterface) object;
		else
			pageCommandInterface = null;
	}

	/**
	 * Provides button interfaces for a listener
	 * @return The button interface list
	 */
	public List<ButtonInterface> provideButtons() {
		if (command == null) {
			logger.error("No command found to provide pagination");
			return new ArrayList<>();
		}

		if (pageCommandInterface == null) {
			logger.error("No page helper found to provide pagination");
			return new ArrayList<>();
		}

		if (command.getInvoke().isEmpty()) {
			logger.error("No invoke found to provide pagination");
			return new ArrayList<>();
		}

		List<ButtonInterface> buttonInterfaces = Arrays.asList(
			new PageLeft(command.getInvoke()) {

				@Override
				public List<EmbedBuilder> getPages(ButtonContext ctx) {
					if (ctx.getEvent().getGuild() == null)
						return null;

					return PaginationResources.buildEmbeds(
						command.getInvoke(),
						ctx.getEventMember().getAvatarUrl(),
						pageCommandInterface.pageStrings(),
						pageCommandInterface.itemsPerPage()
					);
				}

				@Override
				public boolean loopPagination() {
					return pageCommandInterface.loopPagination();
				}
			},
			new PageRight(command.getInvoke()) {

				@Override
				public List<EmbedBuilder> getPages(ButtonContext ctx) {
					if (ctx.getEvent().getGuild() == null)
						return null;

					return PaginationResources.buildEmbeds(
						command.getInvoke(),
						ctx.getEventMember().getAvatarUrl(),
						pageCommandInterface.pageStrings(),
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
				new PageHome(command.getInvoke()) {

					@Override
					public List<EmbedBuilder> getPages(ButtonContext ctx) {
						if (ctx.getEvent().getGuild() == null)
							return null;

						return PaginationResources.buildEmbeds(
							command.getInvoke(),
							ctx.getEventMember().getAvatarUrl(),
							pageCommandInterface.pageStrings(),
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
				new PageFirst(command.getInvoke()) {

					@Override
					public List<EmbedBuilder> getPages(ButtonContext ctx) {
						if (ctx.getEvent().getGuild() == null)
							return null;

						return PaginationResources.buildEmbeds(
							command.getInvoke(),
							ctx.getEventMember().getAvatarUrl(),
							pageCommandInterface.pageStrings(),
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
