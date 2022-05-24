package me.almana;

import me.almana.Tickets.TicketCreator;
import me.almana.Tickets.TicketSetup;
import me.almana.Tickets.TicketWorking;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDA jda;
    public static Guild guild;
    private static JDABuilder builder;

    public static void main(String[] args) throws LoginException, InterruptedException {

        String token = Secret.token;

        builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.watching("TV"));
        builder.addEventListeners(new TicketSetup());
        builder.addEventListeners(new TicketCreator());
        builder.addEventListeners(new TicketWorking());
        jda = builder.build();
        jda.awaitReady();

        guild = jda.getGuilds().get(0);

        guild.upsertCommand("setup", "Setups the ticket.").queue();
        guild.upsertCommand("claim", "Claims a ticket.").queue();
        guild.upsertCommand("close", "Closes a ticket").queue();
    }
}
