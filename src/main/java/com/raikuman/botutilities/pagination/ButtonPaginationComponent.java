package com.raikuman.botutilities.pagination;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public abstract class ButtonPaginationComponent extends ButtonComponent {

    public Pagination pagination;

    public List<EmbedBuilder> updatePages(MessageChannelUnion channel) {
        return pagination.getPaginationPages().getPages(channel, pagination.getOriginalUser());
    }
}
