package com.raikuman.botutilities.invocation.component.pagination;

import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.pagination.PageButtons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;

public class Pagination {

    private final PaginationPages paginationPages;
    private final ComponentHandler componentHandler;
    private boolean isDynamic = false;

    public Pagination(PaginationPages paginationPages, ComponentHandler componentHandler) {
        this.paginationPages = paginationPages;
        this.componentHandler = componentHandler;
    }

    public Pagination(PaginationPages paginationPages, ComponentHandler componentHandler, boolean isDynamic) {
        this.paginationPages = paginationPages;
        this.componentHandler = componentHandler;
        this.isDynamic = isDynamic;
    }

    public void sendPagination(SlashCommandInteractionEvent ctx) {
        User user = ctx.getUser();

        List<EmbedBuilder> pages = paginationPages.getPages(ctx.getChannel(), user);
        paginatePages(pages);
        List<ButtonComponent> buttons = PageButtons.getbuttons(paginationPages, pages, isDynamic);

        InteractionHook interactionHook = ctx.replyEmbeds(pages.get(0).build())
            .setComponents(ComponentBuilder.buildButtons(user, buttons)).complete();

        componentHandler.addButtons(user, interactionHook, buttons);
    }

    public void sendPagination(CommandContext ctx) {
        Message message = ctx.event().getMessage();
        User user = message.getAuthor();

        List<EmbedBuilder> pages = paginationPages.getPages(message.getChannel(), user);
        paginatePages(pages);
        List<ButtonComponent> buttons = PageButtons.getbuttons(paginationPages, pages, isDynamic);

        Message paginationMessage = message.getChannel().sendMessageEmbeds(
            pages.get(0).build()).setComponents(ComponentBuilder.buildButtons(user, buttons)).complete();

        componentHandler.addButtons(user, paginationMessage, buttons);
        ctx.event().getMessage().delete().queue();
    }

    private void sendPagination(Message message, boolean reply) {
        User user = message.getAuthor();

        List<EmbedBuilder> pages = paginationPages.getPages(message.getChannel(), user);
        paginatePages(pages);
        List<ButtonComponent> buttons = PageButtons.getbuttons(paginationPages, pages, isDynamic);

        Message paginationMessage;
        if (reply) {
            paginationMessage = message.replyEmbeds(pages.get(0).build())
                .setComponents(ComponentBuilder.buildButtons(user, buttons)).complete();
        } else {
            paginationMessage = message.getChannel().sendMessageEmbeds(
                pages.get(0).build()).setComponents(ComponentBuilder.buildButtons(user, buttons)).complete();
        }

        componentHandler.addButtons(user, paginationMessage, buttons);
    }

    public static void paginatePages(List<EmbedBuilder> embedBuilders) {
        for (int i = 0; i < embedBuilders.size(); i++) {
            embedBuilders.get(i).setFooter((i + 1) + "/" + embedBuilders.size());
        }
    }
}
