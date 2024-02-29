package com.raikuman.botutilities.invocation.component.pagination;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public abstract class ButtonPaginationComponent implements ButtonComponent {

    public PaginationPages paginationPages;

    public List<EmbedBuilder> updatePages(MessageChannelUnion channel, User user) {
        return paginationPages.getPages(channel, user);
    }
}
