package com.raikuman.botutilities.invocation.component.pagination;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public interface PaginationPages {

    List<EmbedBuilder> getPages(MessageChannelUnion channel, User user);
}
