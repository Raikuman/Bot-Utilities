package com.raikuman.botutilities.pagination;

import com.raikuman.botutilities.invocation.component.pagination.Pagination;
import com.raikuman.botutilities.invocation.component.pagination.PaginationComponent;
import com.raikuman.botutilities.invocation.component.pagination.PaginationPages;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PageButtons {

    public static List<ButtonComponent> getbuttons(PaginationPages paginationPages, List<EmbedBuilder> pages,
                                                   boolean isDynamic) {
        List<ButtonComponent> buttons = new ArrayList<>();
        buttons.add(new PageLeft(paginationPages, pages, isDynamic));
        buttons.add(new PageRight(paginationPages, pages, isDynamic));
        return buttons;
    }

    static class PageLeft extends PaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic;

        PageLeft(PaginationPages paginationPages, List<EmbedBuilder> pages,boolean isDynamic) {
            this.paginationPages = paginationPages;
            this.pages = pages;
            this.isDynamic = isDynamic;
        }

        @Override
        public void handle(ButtonInteractionEvent ctx) {
            if (isDynamic) {
                pages = paginationPages.getPages(ctx.getChannel(), ctx.getUser());
                Pagination.paginatePages(pages);
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
            return "pageleft";
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

    static class PageRight extends PaginationComponent {

        private List<EmbedBuilder> pages;
        private final boolean isDynamic;

        PageRight(PaginationPages paginationPages, List<EmbedBuilder> pages,boolean isDynamic) {
            this.paginationPages = paginationPages;
            this.pages = pages;
            this.isDynamic = isDynamic;
        }

        @Override
        public void handle(ButtonInteractionEvent ctx) {
            if (isDynamic) {
                pages = paginationPages.getPages(ctx.getChannel(), ctx.getUser());
                Pagination.paginatePages(pages);
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
            return "pageright";
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

    private static int getPageNumber(MessageEmbed embed) {
        MessageEmbed.Footer footer = embed.getFooter();
        if (footer == null || footer.getText() == null) {
            return 0;
        }

        String pageString = footer.getText().split("/")[0];
        try {
            return Integer.parseInt(pageString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
