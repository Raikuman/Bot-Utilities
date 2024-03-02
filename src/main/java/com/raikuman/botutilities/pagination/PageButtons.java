package com.raikuman.botutilities.pagination;

import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PageButtons {

    private final Pagination pagination;
    private final String invoke;
    private final int numPages;
    private boolean firstPage, lastMenu, isDynamic;

    public PageButtons(String invoke, Pagination pagination, int numPages) {
        this.firstPage = false;
        this.lastMenu = false;
        this.isDynamic = false;
        this.invoke = invoke;
        this.pagination = pagination;
        this.numPages = numPages;
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

    public static PageButtons getButtons(String invoke, Pagination pagination, int numPages) {
        return new PageButtons(invoke, pagination, numPages);
    }

    public List<ButtonComponent> build() {
        boolean isSinglePage = numPages == 1;

        List<ButtonComponent> buttons = new ArrayList<>();
        if (lastMenu && pagination.getParent() != null) {
            // Add previous pagination button
            buttons.add(new LastMenu(invoke, pagination.getParent(), isDynamic));
        }

        buttons.add(new PageLeft(invoke, pagination, isDynamic, isSinglePage));

        if (firstPage) {
            // Add first page button
            buttons.add(new FirstPage(invoke, pagination, isDynamic, isSinglePage));
        }

        buttons.add(new PageRight(invoke, pagination, isDynamic, isSinglePage));
        return buttons;
    }

    static class PageLeft extends ButtonPaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic, isSinglePage;
        private final String invoke;

        PageLeft(String invoke, Pagination pagination, boolean isDynamic, boolean isSinglePage) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.isSinglePage = isSinglePage;
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
                if (pageNumber != 0) {
                    pageNumber--;
                }

                MessageEditCallbackAction callbackAction = ctx.editMessageEmbeds(pages.get(pageNumber).build());

                // Check for looping
                if (!pagination.getLooping() && pageNumber == 0) {
                    callbackAction = callbackAction.setComponents(updateButtons(ctx, ctx.getButton()));
                }

                callbackAction.queue();
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

        @Override
        public boolean isDisabled() {
            return !pagination.getLooping() || isSinglePage;
        }
    }

    static class PageRight extends ButtonPaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic, isSinglePage;
        private final String invoke;

        PageRight(String invoke, Pagination pagination, boolean isDynamic, boolean isSinglePage) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.isSinglePage = isSinglePage;
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

                MessageEditCallbackAction callbackAction = ctx.editMessageEmbeds(pages.get(pageNumber).build());

                // Check for looping
                if (!pagination.getLooping() && pageNumber == pages.size() - 1) {
                    callbackAction = callbackAction.setComponents(updateButtons(ctx, ctx.getButton()));
                }

                callbackAction.queue();
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

        @Override
        public boolean isDisabled() {
            return isSinglePage;
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
                buttons =
                    PageButtons.getButtons(invoke, pagination.getParent(),
                        pagination.getParent().getPaginationPages().getPages(ctx.getChannel(), ctx.getUser()).size()).setDynamic(isDynamic).build();
            } else {
                buttons = PageButtons.getButtons(invoke, pagination, pages.size()).setDynamic(isDynamic).build();
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
            return Emoji.fromFormatted("\uD83D\uDD19");
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
        private final boolean isDynamic, isSinglePage;
        private final String invoke;

        FirstPage(String invoke, Pagination pagination, boolean isDynamic, boolean isSinglePage) {
            this.pagination = pagination;
            this.pages = new ArrayList<>();
            this.isDynamic = isDynamic;
            this.isSinglePage = isSinglePage;
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

        @Override
        public boolean isDisabled() {
            return isSinglePage;
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

    private static List<ActionRow> updateButtons(ButtonInteractionEvent ctx, Button eventButton) {
        List<ActionRow> actionRows = ctx.getMessage().getActionRows();
        if (actionRows.isEmpty()) return new ArrayList<>();

        List<Button> buttons = new ArrayList<>();
        for (Button button : ctx.getMessage().getButtons()) {
            if (button.equals(eventButton) && !eventButton.isDisabled()) {
                buttons.add(button.asDisabled());
            } else {
                buttons.add(button.asEnabled());
            }
        }

        // Buttons will always be on row 1
        actionRows.set(0, ActionRow.of(buttons));

        return actionRows;
    }
}
