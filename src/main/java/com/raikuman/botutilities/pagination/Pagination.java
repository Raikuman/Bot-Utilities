package com.raikuman.botutilities.pagination;

import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Pagination {

    private static final Logger logger = LoggerFactory.getLogger(Pagination.class);
    private final PaginationPages paginationPages;
    private ComponentHandler componentHandler;
    private boolean isDynamic, hasLastMenu, hasFirstPage, isLooping, ignoreAuthor;
    private List<SelectComponent> selects;
    private List<ButtonComponent> extraButtons;
    private final String invoke;
    private String placeholder;
    private Pagination parent;
    private final User originalUser;

    public Pagination(User user, String invoke, PaginationPages paginationPages, ComponentHandler componentHandler) {
        this.paginationPages = paginationPages;
        this.componentHandler = componentHandler;
        this.isDynamic = false;
        this.hasLastMenu = false;
        this.hasFirstPage = false;
        this.isLooping = true;
        this.ignoreAuthor = false;
        this.selects = new ArrayList<>();
        this.extraButtons = new ArrayList<>();
        this.invoke = invoke;
        this.placeholder = "";
        this.originalUser = user;
    }

    public Pagination(User user, String invoke, PaginationPages paginationPages) {
        this.paginationPages = paginationPages;
        this.componentHandler = null;
        this.isDynamic = false;
        this.hasLastMenu = false;
        this.hasFirstPage = false;
        this.isLooping = true;
        this.ignoreAuthor = false;
        this.selects = new ArrayList<>();
        this.extraButtons = new ArrayList<>();
        this.invoke = invoke;
        this.placeholder = "";
        this.originalUser = user;
    }

    public Pagination setDynamic(boolean dynamic) {
        isDynamic = dynamic;
        return this;
    }

    public Pagination setHasLastMenu(boolean hasLastMenu) {
        this.hasLastMenu = hasLastMenu;
        return this;
    }

    public Pagination setHasFirstPage(boolean hasFirstPage) {
        this.hasFirstPage = hasFirstPage;
        return this;
    }

    public Pagination setLooping(boolean looping) {
        isLooping = looping;
        return this;
    }

    public Pagination setIgnoreAuthor(boolean ignoreAuthor) {
        this.ignoreAuthor = ignoreAuthor;
        return this;
    }

    public Pagination setSelectMenu(String placeholder, List<SelectComponent> selects) {
        this.placeholder = placeholder;
        this.selects = selects;
        return this;
    }

    public Pagination setExtraButtons(ButtonComponent... extraButtons) {
        this.extraButtons = List.of(extraButtons);
        return this;
    }

    public Pagination setPaginationMenu(String placeholder, SelectPaginationComponent... pageSelects) {
        this.placeholder = placeholder;

        List<SelectComponent> selects = new ArrayList<>();
        for (SelectPaginationComponent pageSelect : pageSelects) {
            pageSelect.getPagination().componentHandler = componentHandler;
            pageSelect.getPagination().parent = this;
            selects.add(pageSelect);
        }

        this.selects = selects;
        return this;
    }

    public String getInvoke() {
        return invoke;
    }

    public Pagination getParent() {
        return parent;
    }

    public PaginationPages getPaginationPages() {
        return paginationPages;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public List<SelectComponent> getSelects() {
        return selects;
    }

    public ComponentHandler getComponentHandler() {
        return componentHandler;
    }

    public boolean getLooping() {
        return isLooping;
    }

    public boolean getIgnoreAuthor() {
        return ignoreAuthor;
    }

    public User getOriginalUser() {
        return originalUser;
    }

    public void sendPagination(SlashCommandInteractionEvent ctx) {
        sendSlashPagination(ctx, false);
    }

    public void sendPagination(SlashCommandInteractionEvent ctx, boolean isEphemeral) {
        sendSlashPagination(ctx, isEphemeral);
    }

    private InteractionHook sendSlashPagination(SlashCommandInteractionEvent ctx, boolean isEphemeral) {
        if (componentHandler == null) {
            logger.error("Pagination has no component handler and will not send pagination");
            return null;
        }

        List<EmbedBuilder> pages = paginationPages.getPages(ctx.getChannel(), originalUser);
        if (pages.isEmpty()) {
            ctx.replyEmbeds(
                EmbedResources.error("Error getting pagination", "Could not get pagination for `" + invoke + "`",
                    ctx.getChannel(), originalUser).build()
            ).delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return null;
        }

        paginatePages(this, pages, ctx.getChannel(), ctx.getUser());

        List<ButtonComponent> buttons = PageButtons
            .getButtons(invoke, this, pages.size())
            .setDynamic(isDynamic)
            .setLastMenu(hasLastMenu)
            .setFirstPage(hasFirstPage)
            .build();

        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ComponentBuilder.buildButtons(originalUser, buttons));
        if (!selects.isEmpty()) {
            actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, placeholder, originalUser, selects));
        }

        if (!extraButtons.isEmpty()) {
            actionRows.add(ComponentBuilder.buildButtons(originalUser, extraButtons));
            buttons.addAll(extraButtons);
        }

        InteractionHook interactionHook =
            ctx.replyEmbeds(pages.get(0).build()).setComponents(actionRows).setEphemeral(isEphemeral).complete();

        componentHandler.addButtons(
            originalUser,
            interactionHook,
            buttons);

        if (!selects.isEmpty()) {
            componentHandler.addSelects(
                originalUser,
                interactionHook,
                selects);
        }

        return interactionHook;
    }

    public Message sendPagination(CommandContext ctx) {
        if (componentHandler == null) {
            logger.error("Pagination has no component handler and will not send pagination");
            return null;
        }

        Message message = ctx.event().getMessage();
        List<EmbedBuilder> pages = paginationPages.getPages(message.getChannel(), originalUser);
        if (pages.isEmpty()) {
            MessageResources.embedDelete(
                ctx.event().getChannel(),
                10,
                EmbedResources.error("Error getting pagination", "Could not get pagination for `" + invoke + "`",
                    ctx.event().getChannel(), originalUser)
            );
            return null;
        }

        paginatePages(this, pages, ctx.event().getChannel(), ctx.event().getAuthor());

        List<ButtonComponent> buttons = PageButtons
            .getButtons(invoke, this, pages.size())
            .setDynamic(isDynamic)
            .setLastMenu(hasLastMenu)
            .setFirstPage(hasFirstPage)
            .build();

        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ComponentBuilder.buildButtons(originalUser, buttons));
        if (!selects.isEmpty()) {
            actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, placeholder, originalUser, selects));
        }

        if (!extraButtons.isEmpty()) {
            actionRows.add(ComponentBuilder.buildButtons(originalUser, extraButtons));
            buttons.addAll(extraButtons);
        }

        Message paginationMessage = message.getChannel()
            .sendMessageEmbeds(pages.get(0).build()).setComponents(actionRows).complete();

        componentHandler.addButtons(
            originalUser,
            paginationMessage,
            buttons);

        if (!selects.isEmpty()) {
            componentHandler.addSelects(
                originalUser,
                paginationMessage,
                selects);
        }

        ctx.event().getMessage().delete().queue();
        return paginationMessage;
    }

    public InteractionHook sendPagination(StringSelectInteractionEvent ctx) {
        if (componentHandler == null) {
            logger.error("Pagination has no component handler and will not send pagination");
            return null;
        }

        Message message = ctx.getMessage();
        List<EmbedBuilder> pages = paginationPages.getPages(message.getChannel(), originalUser);
        if (pages.isEmpty()) {
            MessageResources.embedDelete(
                ctx.getChannel(),
                10,
                EmbedResources.error("Error getting pagination", "Could not get pagination for `" + invoke + "`",
                    ctx.getChannel(), originalUser)
            );
            ctx.deferEdit().queue();
            return null;
        }

        paginatePages(this, pages, ctx.getChannel(), originalUser);

        List<ButtonComponent> buttons = PageButtons
            .getButtons(invoke, this, pages.size())
            .setDynamic(isDynamic)
            .setLastMenu(hasLastMenu)
            .setFirstPage(hasFirstPage)
            .build();

        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ComponentBuilder.buildButtons(originalUser, buttons));
        if (!selects.isEmpty()) {
            actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, placeholder, originalUser, selects));
        }

        if (!extraButtons.isEmpty()) {
            actionRows.add(ComponentBuilder.buildButtons(originalUser, extraButtons));
            buttons.addAll(extraButtons);
        }

        InteractionHook interactionHook = ctx
            .editMessageEmbeds(pages.get(0).build())
            .setComponents(actionRows)
            .complete();

        componentHandler.addButtons(
            originalUser,
            interactionHook,
            buttons);

        if (!selects.isEmpty()) {
            componentHandler.addSelects(
                originalUser,
                interactionHook,
                selects);
        }

        return interactionHook;
    }

    public static void paginatePages(Pagination pagination, List<EmbedBuilder> embedBuilders,
                                     MessageChannelUnion messageChannelUnion, User user) {
        // Get page directory
        Pagination currentPagination = pagination;
        StringBuilder pageDirectory = new StringBuilder();
        pageDirectory.append(uppercaseInvoke(currentPagination.invoke));
        while ((currentPagination = currentPagination.getParent()) != null) {
            pageDirectory.insert(0, uppercaseInvoke(currentPagination.invoke) + " > ");
        }

        for (int i = 0; i < embedBuilders.size(); i++) {
            embedBuilders.get(i)
                .setAuthor(String.valueOf(pageDirectory), null, user.getEffectiveAvatarUrl())
                .setFooter((i + 1) + "/" + embedBuilders.size() + " " + messageChannelUnion.getName())
                .setTimestamp(Instant.now());
        }
    }

    private static String uppercaseInvoke(String invoke) {
        return invoke.substring(0, 1).toUpperCase() + invoke.substring(1);
    }

    public static PaginationPages buildPages(User user, MessageChannelUnion channel, String title, Color color,
                                             List<String> strings) {
        // Use default 550 characters per page
        return createPaginationPages(user, channel, title, color, strings, 550);
    }

    public static PaginationPages buildPages(User user, MessageChannelUnion channel, String title, Color color,
                                             List<String> strings, int charactersPerPage) {
        return createPaginationPages(user, channel, title, color, strings, charactersPerPage);
    }

    private static PaginationPages createPaginationPages(User user, MessageChannelUnion channel, String title,
                                                         Color color, List<String> strings, int charactersPerPage) {
        List<EmbedBuilder> pages = new ArrayList<>();
        StringBuilder pageBuilder = new StringBuilder("```asciidoc\n");
        int currentCharacterCount = 0;
        for (int i = 0; i < strings.size(); i++) {
            if (i == 0) {
                pageBuilder
                    .append(strings.get(i));
            } else {
                pageBuilder
                    .append("\n\n")
                    .append(strings.get(i));
            }

            currentCharacterCount += strings.get(i).length();
            if (currentCharacterCount >= charactersPerPage || i == strings.size() - 1) {
                currentCharacterCount = 0;
                pageBuilder.append("```");
                pages.add(EmbedResources.defaultResponse(color, title, pageBuilder.toString(), channel, user));
                pageBuilder = new StringBuilder("```asciidoc\n");
            }
        }

        return (channel1, user1) -> pages;
    }
}
