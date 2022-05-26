package me.almana.invitetrack;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InviteTracking extends ListenerAdapter {
    private final Map<String, InviteData> inviteCache = new ConcurrentHashMap<>();


    @Override
    public void onGuildInviteCreate(final GuildInviteCreateEvent event)
    {
        final String code = event.getCode();
        final InviteData inviteData = new InviteData(event.getInvite());
        inviteCache.put(code, inviteData);
    }

    @Override
    public void onGuildInviteDelete(final GuildInviteDeleteEvent event)
    {
        final String code = event.getCode();
        inviteCache.remove(code);
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event)
    {
        final Guild guild = event.getGuild();
        final User user = event.getUser();
        final Member selfMember = guild.getSelfMember();

        if (!selfMember.hasPermission(Permission.MANAGE_SERVER) || user.isBot())
            return;

        guild.retrieveInvites().queue(retrievedInvites ->
        {
            for (final Invite retrievedInvite : retrievedInvites)
            {
                final String code = retrievedInvite.getCode();
                final InviteData cachedInvite = inviteCache.get(code);
                if (cachedInvite == null)
                    continue;
                if (retrievedInvite.getUses() == cachedInvite.getUses())
                    continue;
                cachedInvite.incrementUses();
                final String pattern = "User %s used invite with url %s, created by %s to join.";
                final String tag = user.getAsTag();
                final String url = retrievedInvite.getUrl();
                final String inviterTag = retrievedInvite.getInviter().getAsTag();
                final String toLog = String.format(pattern, tag, url, inviterTag);
                System.out.println(toLog);
                break;
            }
        });
    }

    @Override
    public void onGuildReady(final GuildReadyEvent event)                                             // gets fired when a guild has finished setting up upon booting the bot, lets try to cache its invites
    {
        final Guild guild = event.getGuild();                                                         // get the guild that has finished setting up
        attemptInviteCaching(guild);                                                                  // attempt to store guild's invites
    }

    @Override
    public void onGuildJoin(final GuildJoinEvent event)                                               // gets fired when your bot has joined a guild, lets try to store its invites
    {
        final Guild guild = event.getGuild();                                                         // get the guild your bot has joined
        attemptInviteCaching(guild);                                                                  // attempt to store guild's invites
    }

    @Override
    public void onGuildLeave(final GuildLeaveEvent event)                                             // gets fired when your bot has left a guild, uncache all invites for it
    {
        final long guildId = event.getGuild().getIdLong();                                            // get the id of the guild your bot has left
        inviteCache.entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);           // remove entry from the map if its value's guild id is the one your bot has left
    }

    private void attemptInviteCaching(final Guild guild)                                              // helper method to prevent duplicate code for GuildReadyEvent and GuildJoinEvent
    {
        final Member selfMember = guild.getSelfMember();                                              // get your bot's member object for this guild

        if (!selfMember.hasPermission(Permission.MANAGE_SERVER))                                      // check if your bot doesn't have MANAGE_SERVER permission to retrieve the invites, if true, return
            return;

        guild.retrieveInvites().queue(retrievedInvites ->                                             // retrieve all guild's invites
        {
            retrievedInvites.forEach(retrievedInvite ->                                               // iterate over invites..
                    inviteCache.put(retrievedInvite.getCode(), new InviteData(retrievedInvite)));     // and store them
        });
    }
}
