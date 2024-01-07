package com.raikuman.botutilities.defaults;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEventListener.class);

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + DatabaseEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    ////////////////////////////////////////
    // Guild join/leave                   //
    ////////////////////////////////////////
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        DefaultDatabaseHandler.removeGuild(event.getGuild());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        DefaultDatabaseHandler.addGuild(event.getGuild());
    }

    ////////////////////////////////////////
    // Member join/leave                  //
    ////////////////////////////////////////
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        int guildId = DefaultDatabaseHandler.getGuildId(event.getGuild());
        int userId = DefaultDatabaseHandler.addUser(event.getUser());

        if (guildId == -1 || userId == -1) {
            logger.error("An error occurred adding a joining user to guild: " + event.getUser().getEffectiveName() +
                ":" + event.getUser().getId() + " " + event.getGuild().getName() + ":" + event.getGuild().getId());
            return;
        }

        DefaultDatabaseHandler.addMember(guildId, userId);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        int guildId = DefaultDatabaseHandler.getGuildId(event.getGuild());
        int userId = DefaultDatabaseHandler.getUserId(event.getUser());

        if (guildId == -1 || userId == -1) {
            logger.error("An error occurred removing a leaving user from guild: " + event.getUser().getEffectiveName() +
                ":" + event.getUser().getId() + " " + event.getGuild().getName() + ":" + event.getGuild().getId());
            return;
        }

        DefaultDatabaseHandler.removeMember(guildId, userId);
    }


}
