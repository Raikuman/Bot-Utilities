package com.raikuman.botutilities.invocation.component.pagination;

import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PageButtons {

    private final Pagination pagination;
    private final String invoke;
    private boolean firstPage, lastMenu, isDynamic;

    public PageButtons(String invoke, Pagination pagination) {
        this.firstPage = false;
        this.lastMenu = false;
        this.isDynamic = false;
        this.invoke = invoke;
        this.pagination = pagination;
    }

    public PageButtons setFirstPage(boolean firstPage) {
        this.firstPage = firstPage;
        return this;
    }

    public PageButtons setLastMenu(boolean lastMenu) {
        this.lastMenu = lastMenu;
        return this;
    }

    public PageButtons setDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
        return this;
    }

    public static PageButtons getButtons(String invoke, Pagination pagination) {
        return new PageButtons(invoke, pagination);
    }

    public List<ButtonComponent> build() {
        List<ButtonComponent> buttons = new ArrayList<>();
        if (lastMenu && pagination.getParent() != null) {
            // Add previous pagination button
            buttons.add(new LastMenu(invoke, pagination.getParent(), isDynamic));
        }

        if (firstPage) {
            // Add first page button
            buttons.add(new FirstPage(invoke, pagination, isDynamic));
        }

        buttons.add(new PageLeft(invoke, pagination, isDynamic));
        buttons.add(new PageRight(invoke, pagination, isDynamic));
        return buttons;
    }

    static class PageLeft extends ButtonPaginationComponent {


        private List<EmbedBuilder> pages;
        private final boolean isDynamic;
        private final String invoke;

        PageLeft(String invoke, Pagination pagination, boolean isDynamic) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.invoke = invoke;
        }

        @Override
        public void handle(ButtonInteractionEvent ctx) {
            if (isDynamic || pages.isEmpty()) {
                pages = updatePages(ctx.getChannel(), ctx.getUser());
                Pagination.paginatePages(pagination, pages, ctx.getChannel(), ctx.getUser());
            }

            Optional<MessageEmbed> messageEmbed = ctx.getMessage().getEmbeds().stream().findFirst();
            if (messageEmbed.isPresent()) {
                int pageNumber = getPageNumber(messageEmbed.get()) - 1;
                if (pageNumber == 0) {
                    pageNumber = pages.size() - 1;
                } else {
                    pageNumber--;
                }

                ctx.editMessageEmbeds(pages.get(pageNumber).build()).queue();
            } else {
                ctx.deferEdit().queue();
            }
        }

        @Override
        public String getInvoke() {
            return invoke + "pageleft";
        }

        @Override
        public Emoji displayEmoji() {
            return Emoji.fromFormatted("⬅️");
        }

        @Override
        public String displayLabel() {
            return null;
        }

        @Override
        public ButtonStyle buttonStyle() {
            return ButtonStyle.SECONDARY;
        }
    }

    static class PageRight extends ButtonPaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic;
        private final String invoke;

        PageRight(String invoke, Pagination pagination, boolean isDynamic) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.invoke = invoke;
        }

        @Override
        public void handle(ButtonInteractionEvent ctx) {
            if (isDynamic || pages.isEmpty()) {
                pages = updatePages(ctx.getChannel(), ctx.getUser());
                Pagination.paginatePages(pagination, pages, ctx.getChannel(), ctx.getUser());
            }

            Optional<MessageEmbed> messageEmbed = ctx.getMessage().getEmbeds().stream().findFirst();
            if (messageEmbed.isPresent()) {
                int pageNumber = getPageNumber(messageEmbed.get()) - 1;
                if (pageNumber >= pages.size() - 1) {
                    pageNumber = 0;
                } else {
                    pageNumber++;
                }

                ctx.editMessageEmbeds(pages.get(pageNumber).build()).queue();
            } else {
                ctx.deferEdit().queue();
            }
        }

        @Override
        public String getInvoke() {
            return invoke + "pageright";
        }

        @Override
        public Emoji displayEmoji() {
            return Emoji.fromFormatted("➡️");
        }

        @Override
        public String displayLabel() {
            return null;
        }

        @Override
        public ButtonStyle buttonStyle() {
            return ButtonStyle.SECONDARY;
        }
    }

    static class LastMenu extends ButtonPaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic;
        private final String invoke;

        LastMenu(String invoke, Pagination pagination, boolean isDynamic) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.invoke = invoke;
        }

        @Override
        public void handle(ButtonInteractionEvent ctx) {
            if (isDynamic || pages.isEmpty()) {
                pages = updatePages(ctx.getChannel(), ctx.getUser());
                Pagination.paginatePages(pagination, pages, ctx.getChannel(), ctx.getUser());
            }

            // Update components
            User user = ctx.getUser();
            List<ButtonComponent> buttons;
            if (pagination.getParent() != null) {
                buttons = PageButtons.getButtons(invoke, pagination.getParent()).setDynamic(isDynamic).build();
            } else {
                buttons = PageButtons.getButtons(invoke, pagination).setDynamic(isDynamic).build();
            }

            List<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(ComponentBuilder.buildButtons(user, buttons));
            if (!pagination.getSelects().isEmpty()) {
                actionRows.add(ComponentBuilder.buildStringSelectMenu(invoke, pagination.getPlaceholder(), user,
                    pagination.getSelects()));
            }

            // Update pagination
            InteractionHook interactionHook = ctx.editMessageEmbeds(pages.get(0).build()).setComponents(actionRows).complete();

            pagination.getComponentHandler().addButtons(
                user,
                interactionHook,
                buttons);

            if (!pagination.getSelects().isEmpty()) {
                pagination.getComponentHandler().addSelects(
                    user,
                    interactionHook,
                    pagination.getSelects());
            }
        }

        @Override
        public String getInvoke() {
            return invoke + "lastmenu";
        }

        @Override
        public Emoji displayEmoji() {
            return Emoji.fromFormatted("↩️");
        }

        @Override
        public String displayLabel() {
            return null;
        }

        @Override
        public ButtonStyle buttonStyle() {
            return ButtonStyle.SECONDARY;
        }
    }

    static class FirstPage extends ButtonPaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic;
        private final String invoke;

        FirstPage(String invoke, Pagination pagination, boolean isDynamic) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.invoke = invoke;
        }

        @Override
        public void handle(ButtonInteractionEvent ctx) {
            if (isDynamic || pages.isEmpty()) {
                pages = updatePages(ctx.getChannel(), ctx.getUser());
                Pagination.paginatePages(pagination, pages, ctx.getChannel(), ctx.getUser());
            }

            // Go to first page
            ctx.editMessageEmbeds(pages.get(0).build()).queue();
        }

        @Override
        public String getInvoke() {
            return invoke + "firstpage";
        }

        @Override
        public Emoji displayEmoji() {
            return Emoji.fromFormatted("1️⃣");
        }

        @Override
        public String displayLabel() {
            return null;
        }

        @Override
        public ButtonStyle buttonStyle() {
            return ButtonStyle.SECONDARY;
        }
    }

    private static int getPageNumber(MessageEmbed embed) {
        MessageEmbed.Footer footer = embed.getFooter();
        if (footer == null || footer.getText() == null) {
            return 0;
        }

        String pageString = footer.getText().split(" ")[0].split("/")[0];
        try {
            return Integer.parseInt(pageString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
