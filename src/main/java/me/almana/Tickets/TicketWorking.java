package me.almana.Tickets;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TicketWorking extends ListenerAdapter {

    String commCategory = TicketSetup.commCategory;
    String appCategory = TicketSetup.appCategory;

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        super.onChannelCreate(event);

        if (event.getChannel().getType() == ChannelType.TEXT) {

            TextChannel channel = (TextChannel) event.getChannel();

            if (channel.getParentCategory().getName().equalsIgnoreCase(commCategory)) {

                embedBasic("COMMISSION", """
                        1. What category does your commision fall under?
                        2. What is your budget?
                        3. When is your deadline?
                        4. What do you need? (Please elaborate)
                        5. Do you have any references related to the commission?
                        6. Did you read our terms of services?
                        7. Who referred you to arcade studios?
                        8. Would you like to ask anything else?""", Color.GREEN, channel);
            } else if (channel.getParentCategory().getName().equalsIgnoreCase(appCategory)) {

                embedBasic("APPLICATION", """
                        1. Why do you want to join Arcade studios?
                        2. Which field are you interested in?
                        3. What is your age?
                        4. Do you have any link-able portfolio?
                        5. Have you ever worked with any other teams?""", Color.CYAN, channel);
            }
        }
    }

    public void embedBasic(String ticketName, String description, Color color, TextChannel channel) {

        channel.getManager().setTopic("**UNCLAIMED**").queue();
        EmbedBuilder e = new EmbedBuilder();

        e.setTitle(ticketName);
        e.setDescription(description);
        e.setColor(color);
        channel.sendMessageEmbeds(e.build()).queue();
    }
}