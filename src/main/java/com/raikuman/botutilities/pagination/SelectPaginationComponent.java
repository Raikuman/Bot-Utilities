package com.raikuman.botutilities.pagination;

import com.raikuman.botutilities.invocation.type.SelectComponent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class SelectPaginationComponent extends SelectComponent {

    private final Pagination pagination;
    private final String label;
    private final boolean ignoreAuthor;

    public SelectPaginationComponent(String label, Pagination pagination) {
        this.label = label;
        this.pagination = pagination;
        this.ignoreAuthor = false;
    }

    public SelectPaginationComponent(String label, boolean ignoreAuthor, Pagination pagination) {
        this.ignoreAuthor = ignoreAuthor;
        this.label = label;
        this.pagination = pagination;
    }

    @Override
    public void handle(StringSelectInteractionEvent ctx) {
        pagination.sendPagination(ctx);
    }

    @Override
    public String getInvoke() {
        return pagination.getInvoke();
    }

    @Override
    public String displayLabel() {
        return label;
    }

    public Pagination getPagination() {
        return pagination;
    }

    @Override
    public boolean ignoreAuthor() {
        return ignoreAuthor;
    }
}
