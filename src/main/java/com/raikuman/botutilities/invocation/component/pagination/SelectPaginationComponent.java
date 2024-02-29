package com.raikuman.botutilities.invocation.component.pagination;

import com.raikuman.botutilities.invocation.type.SelectComponent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class SelectPaginationComponent implements SelectComponent {

    private final Pagination pagination;
    private final String label;

    public SelectPaginationComponent(String label, Pagination pagination) {
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
}
