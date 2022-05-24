package me.almana.Tickets;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TicketSetup extends ListenerAdapter {

    public static String commCategory = "commission";
    public static String suppCategory = "support";
    public static String appCategory = "application";
    public static Role claimRole;
    public static String transcriptChannel = "lol";

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        claimRole = event.getGuild().getRoleById(931406972274757773L); // DONT FORGET TO CHANGE THIS

        if (event.getName().equals("setup")) {

            EmbedBuilder em = new EmbedBuilder();
            em.setTitle("**OPEN A TICKET FOR YOUR REQUIREMENTS!**");
            em.setColor(Color.RED);
            em.addField("Commission", ":package: - Get a comission ready.", false);
            em.addField("Support", ":pencil: - Need some support? Ask us now.", false);
            em.addField("Apply", ":key: - Apply to become a part of this amazing team.", false);

            event.replyEmbeds(em.build()).addActionRow(
                    Button.primary("comm", Emoji.fromUnicode("\uD83D\uDCE6")),
                    Button.primary("supp", Emoji.fromUnicode("\uD83D\uDCDD")),
                    Button.primary("app", Emoji.fromUnicode("\uD83D\uDD11"))
            ).queue();
        } else if (event.getName().equals("claim")) {

            TextChannel channel = event.getTextChannel();
            Member member = event.getMember();

            if (event.getTextChannel().getTopic().equals("**UNCLAIMED**")) {

                if (member.getRoles().contains(claimRole)) {

                    channel.getManager().setTopic("Claimed by:- " + member.getEffectiveName()).queue();
                    event.reply("This ticket is now claimed. Claimer: " +member.getAsMention()).queue();
                }
            } else if (channel.getTopic().contains("Claimed by:- ")) {

                if (member.getRoles().contains(claimRole)) {

                    String[] x = channel.getTopic().split(" ");

                    event.reply("Ticket is already claimed by " + x[x.length - 1]).setEphemeral(true).queue();
                }
            }
        } else if (event.getName().equals("close")) {

            Member member = event.getMember();
            TextChannel channel = event.getTextChannel();

            if (member.getRoles().contains(claimRole)) {

                if (channel.getParentCategory().getName().equalsIgnoreCase(commCategory) || channel.getParentCategory().getName().equalsIgnoreCase(suppCategory) || channel.getParentCategory().getName().equalsIgnoreCase(appCategory)) {

                    event.reply("Closing ticket...").queue();
                    List<Message> messages = new ArrayList<>();
                    channel.getIterableHistory().cache(false).forEach(messages::add);
                    messages.removeIf(message -> message.getAuthor().isBot() || message.getAuthor().isSystem());
                    Collections.reverse(messages);

                    if (messages.size() > 0) {

                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = formatter.format(date);

                        EmbedBuilder e = new EmbedBuilder();
                        e.setTitle(event.getTextChannel().getName());
                        e.setColor(Color.green);
                        e.setDescription("**Transcript**");
                        e.setFooter(strDate);
                        for (Message m: messages) {

                            e.appendDescription("\n" + m.getContentRaw());
                        }
                        event.getGuild().getTextChannelsByName(transcriptChannel, true).get(0).sendMessageEmbeds(e.build()).queue();
                        channel.delete().queue();
                    }
                } else {

                    event.reply("This channel cannot be closed.").setEphemeral(true).queue();
                }
            }
        }
    }
}
