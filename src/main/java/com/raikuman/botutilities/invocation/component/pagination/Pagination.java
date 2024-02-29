package com.raikuman.botutilities.invocation.component.pagination;

import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.botutilities.invocation.component.pagination.tool.PageButtons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pagination {

    private static final Logger logger = LoggerFactory.getLogger(Pagination.class);
    private PaginationPages paginationPages;
    private ComponentHandler componentHandler;
    private boolean isDynamic;
    private List<SelectComponent> selects;
    private final String invoke;
    private String placeholder;
    private final HashMap<String, Pagination> children;
    private Pagination parent;

    public Pagination(String invoke, PaginationPages paginationPages, ComponentHandler componentHandler) {
        this.paginationPages = paginationPages;
        this.componentHandler = componentHandler;
        this.isDynamic = false;
        this.selects = new ArrayList<>();
        this.invoke = invoke;
        this.placeholder = "";
        this.children = new HashMap<>();
    }

    public Pagination(String invoke, PaginationPages paginationPages) {
        this.paginationPages = paginationPages;
        this.componentHandler = null;
        this.isDynamic = false;
        this.selects = new ArrayList<>();
        this.invoke = invoke;
        this.placeholder = "";
        this.children = new HashMap<>();
    }

    public Pagination setDynamic(boolean dynamic) {
        isDynamic = dynamic;
        return this;
    }

    public Pagination setSelectMenu(String placeholder, List<SelectComponent> selects) {
        this.placeholder = placeholder;
        this.selects = selects;
        return this;
    }

    public Pagination setPaginationMenu(String placeholder, SelectPaginationComponent... pageSelects) {
        this.placeholder = placeholder;

        List<SelectComponent> selects = new ArrayList<>();
        for (SelectPaginationComponent pageSelect : pageSelects) {
            pageSelect.getPagination().setComponentHandler(componentHandler);
            pageSelect.getPagination().setParent(this);
            selects.add(pageSelect);

            children.put(pageSelect.getInvoke(), pageSelect.getPagination());
        }

        this.selects = selects;
        return this;
    }

    public void setComponentHandler(ComponentHandler componentHandler) {
        this.componentHandler = componentHandler;
    }

    public void setParent(Pagination pagination) {
        this.parent = pagination;
    }

    public String getInvoke() {
        return invoke;
    }

    public void sendPagination(SlashCommandInteractionEvent ctx) {
        if (componentHandler == null) {
            logger.error("Pagination has no component handler and will not send pagination");
            return;
        }

        User user = ctx.getUser();
        List<ButtonComponent> buttons = PageButtons.getButtons(invoke, paginationPages, isDynamic).build();

        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ComponentBuilder.buildButtons(user, buttons));
        if (!selects.isEmpty()) {
            actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, placeholder, user, selects));
        }

        List<EmbedBuilder> pages = paginationPages.getPages(ctx.getChannel(), user);
        paginatePages(pages);
        InteractionHook interactionHook = ctx.replyEmbeds(pages.get(0).build()).setComponents(actionRows).complete();

        componentHandler.addButtons(
            user,
            interactionHook,
            buttons);

        if (!selects.isEmpty()) {
            componentHandler.addSelects(
                user,
                interactionHook,
                selects);
        }
    }

    public void sendPagination(CommandContext ctx) {
        if (componentHandler == null) {
            logger.error("Pagination has no component handler and will not send pagination");
            return;
        }

        Message message = ctx.event().getMessage();
        User user = message.getAuthor();
        List<ButtonComponent> buttons = PageButtons.getButtons(invoke, paginationPages, isDynamic).build();

        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ComponentBuilder.buildButtons(user, buttons));
        if (!selects.isEmpty()) {
            actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, placeholder, user, selects));
        }

        List<EmbedBuilder> pages = paginationPages.getPages(message.getChannel(), user);
        paginatePages(pages);
        Message paginationMessage = message.getChannel()
            .sendMessageEmbeds(pages.get(0).build()).setComponents(actionRows).complete();

        componentHandler.addButtons(
            user,
            paginationMessage,
            buttons);

        if (!selects.isEmpty()) {
            componentHandler.addSelects(
                user,
                paginationMessage,
                selects);
        }

        ctx.event().getMessage().delete().queue();
    }

    public void sendPagination(StringSelectInteractionEvent ctx) {
        if (componentHandler == null) {
            logger.error("Pagination has no component handler and will not send pagination");
            return;
        }

        Message message = ctx.getMessage();
        User user = ctx.getUser();
        List<ButtonComponent> buttons = PageButtons.getButtons(invoke, paginationPages, isDynamic).build();

        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ComponentBuilder.buildButtons(user, buttons));
        if (!selects.isEmpty()) {
            actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, placeholder, user, selects));
        }

        List<EmbedBuilder> pages = paginationPages.getPages(message.getChannel(), user);
        paginatePages(pages);

        InteractionHook hook = ctx.getInteraction()
            .editMessageEmbeds(pages.get(0).build())
            .setComponents(actionRows)
            .complete();

        componentHandler.addButtons(
            user,
            hook,
            buttons);

        if (!selects.isEmpty()) {
            componentHandler.addSelects(
                user,
                hook,
                selects);
        }
    }

    public static void paginatePages(List<EmbedBuilder> embedBuilders) {
        for (int i = 0; i < embedBuilders.size(); i++) {
            embedBuilders.get(i).setFooter((i + 1) + "/" + embedBuilders.size());
        }
    }
}
