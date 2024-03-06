package com.raikuman.botutilities.defaults.invocation;

import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.invocation.type.Slash;
import com.raikuman.botutilities.pagination.Pagination;
import com.raikuman.botutilities.pagination.PaginationPages;
import com.raikuman.botutilities.pagination.SelectPaginationComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Help extends Slash {

    private final String invoke, description;
    private final List<Category> categories;
    private final List<Slash> slashes;
    private final List<Command> commands;
    private static final Color HELP_COLOR = Color.decode("#10b341");

    public Help(String invoke, String description, List<Category> categories, List<Slash> slashes, List<Command> commands) {
        this.invoke = invoke;
        this.description = description;
        this.categories = categories;
        this.slashes = slashes;
        this.commands = commands;
    }

    @Override
    public void handle(SlashCommandInteractionEvent ctx) {
        Guild guild = ctx.getGuild();

        List<SelectPaginationComponent> selectPaginationComponents = new ArrayList<>();
        StringBuilder homeBuilder = new StringBuilder("Use the menu to view commands in a category!\n\n");
        for (int i = 0; i < categories.size(); i++) {
            List<String> categoryStrings = new ArrayList<>();
            int numInvokes = 0;

            // Retrieve slashes in category
            for (Slash slash : slashes) {
                if (slash.getCategory() == null) {
                    continue;
                }

                if (slash.getCategory().isEqual(categories.get(i))) {
                    categoryStrings.add(buildInvokeString(
                        guild,
                        true,
                        slash.getInvoke(),
                        null,
                        buildOptionsString(slash.getCommandData().getOptions()),
                        slash.getDescription()));

                    numInvokes++;
                }
            }

            // Retrieve commands in category
            for (Command command : commands) {
                if (command.getCategory() != null && command.getCategory().isEqual(categories.get(i))) {
                    categoryStrings.add(buildInvokeString(
                        guild,
                        false,
                        command.getInvoke(),
                        command.getAliases(),
                        command.getUsage(),
                        command.getDescription()));

                    numInvokes++;
                }
            }

            String categoryFormatted = categories.get(i).getCategory().substring(0, 1).toUpperCase() +
                categories.get(i).getCategory().substring(1);

            // Build home strings
            if (i != 0) {
                homeBuilder
                    .append("\n");
            }

            homeBuilder
                .append(categories.get(i).getEmoji().getFormatted())
                .append(" **")
                .append(categoryFormatted)
                .append("**\n*")
                .append(numInvokes)
                .append(" commands*\n");

            // Paginate strings
            selectPaginationComponents.add(
                new SelectPaginationComponent(
                    categoryFormatted,
                    new Pagination(ctx.getUser(), categoryFormatted, Pagination.buildPages(ctx.getUser(), ctx.getChannel(),
                        categoryFormatted, HELP_COLOR, categoryStrings))
                        .setHasLastMenu(true)
                        .setHasFirstPage(true)
                )
            );
        }

        // Build home page
        PaginationPages homePages = (channel, user) -> {
            String prefix = DefaultDatabaseHandler.getPrefix(ctx.getGuild());

            String helpBuilder = "A command that has multiple aliases will be listed with parenthesis.\n***" +
                prefix +
                "command (c, com, comm)***\n\n" +
                "A command that requires parameters will be listed with greater/less than symbols.\n***" +
                prefix +
                "command <# of things>***\n\n" +
                "Commands can also have optional parameters listed with a parenthesis around greater/less" +
                " than symbols.\n***" +
                prefix +
                "command (<user>)***";

            return List.of(
                EmbedResources.defaultResponse(
                    HELP_COLOR,
                    "Radio Commands",
                    homeBuilder.toString(),
                    channel,
                    user
                ),
                EmbedResources.defaultResponse(
                    HELP_COLOR,
                    "Radio Commands",
                    helpBuilder,
                    channel,
                    user
                )
            );
        };

        new Pagination(ctx.getUser(), getInvoke(), homePages, componentHandler)
            .setPaginationMenu(
                "View commands in category",
                selectPaginationComponents.toArray(new SelectPaginationComponent[0])
            ).sendPagination(ctx, true);
    }

    @Override
    public String getInvoke() {
        return invoke;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getInvoke(), getDescription());
    }

    @Override
    public Category getCategory() {
        return super.getCategory();
    }

    private String buildInvokeString(Guild guild, boolean isSlash, String invoke, List<String> aliases, String usage,
                                     String description) {
        String prefix;
        if (isSlash) {
            prefix = "/";
        } else {
            prefix = DefaultDatabaseHandler.getPrefix(guild);
        }
        if (prefix.isBlank()) return "";

        String aliasString = "";
        if (aliases != null && !aliases.isEmpty()) {
            StringBuilder aliasBuilder = new StringBuilder()
                .append("(");

            for (int i = 0; i < aliases.size(); i++) {
                if (i == 0) {
                    aliasBuilder
                        .append(aliases.get(i));
                } else {
                    aliasBuilder
                        .append(", ")
                        .append(aliases.get(i));
                }
            }

            aliasBuilder
                .append(")");

            aliasString = String.valueOf(aliasBuilder);
        }

        StringBuilder invokeBuilder = new StringBuilder()
            .append(prefix)
            .append(invoke);

        if (!aliasString.isBlank()) {
            invokeBuilder
                .append(" ")
                .append(aliasString);
        }

        if (!usage.isBlank()) {
            invokeBuilder
                .append(" ")
                .append(usage);
        }

        invokeBuilder
            .append(" :: ")
            .append(description);

        return invokeBuilder.toString();
    }

    private String buildOptionsString(List<OptionData> options) {
        List<String> optionStrings = new ArrayList<>();
        for (OptionData option : options) {
            StringBuilder optionBuilder = new StringBuilder();

            // Handle optional opening
            if (option.isRequired()) {
                optionBuilder
                    .append("<");
            } else {
                optionBuilder
                    .append("(<");
            }

            optionBuilder
                .append(option.getName());

            if (!option.getDescription().isBlank()) {
                optionBuilder
                    .append(": ")
                    .append(option.getDescription());
            }

            // Handle optional closing
            if (option.isRequired()) {
                optionBuilder
                    .append(">");
            } else {
                optionBuilder
                    .append(">)");
            }

            optionStrings.add(optionBuilder.toString());
        }

        return String.join(" ", optionStrings);
    }
}
