package com.raikuman.botutilities.invocation.component.pagination;

import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.pagination.PageButtons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

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

    public void sendPagination(MessageChannelUnion channel, User user) {
        List<EmbedBuilder> pages = paginationPages.getPages(channel, user);
        paginatePages(pages);
        List<ButtonComponent> buttons = PageButtons.getbuttons(paginationPages, pages, isDynamic);

        Message message = channel.sendMessageEmbeds(
            pages.get(0).build()).setComponents(ComponentBuilder.buildButtons(user, buttons)
        ).complete();

        componentHandler.addButtons(user, message, buttons);
    }

    public static void paginatePages(List<EmbedBuilder> embedBuilders) {
        for (int i = 0; i < embedBuilders.size(); i++) {
            embedBuilders.get(i).setFooter((i + 1) + "/" + embedBuilders.size());
        }
    }
}
