package me.almana.Tickets;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TicketCreator extends ListenerAdapter {

    String commId = "comm";
    String suppId = "supp";
    String appId = "app";
    String commName = "commission";
    String suppName = "support";
    String appName = "application";

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);

        if (event.getComponentId().equalsIgnoreCase(commId)) {

            TextInput line1 = TextInput.create("l1", "What category does your commision fall under?What is your budget?When is your deadline?", TextInputStyle.PARAGRAPH).build();
            TextInput line2 = TextInput.create("l2", "What do you need? (Please elaborate)", TextInputStyle.PARAGRAPH).build();
            TextInput line3 = TextInput.create("l3", "Do you have any references related to the commission?Did you read our terms of services?", TextInputStyle.PARAGRAPH).build();
            TextInput line4 = TextInput.create("l4", "Who referred you to arcade studios?Would you like to ask anything else?", TextInputStyle.PARAGRAPH).build();

            Modal modal = Modal.create("id", "Commission").addActionRow(line1, line2, line3, line4).build();

            createTicket(commName, commName, event, event.getMember());
            event.replyModal(modal).queue();
        } else if (event.getComponentId().equalsIgnoreCase(suppId)) {

            createTicket(suppName, suppName, event, event.getMember());
            event.reply("Your ticket was opened").setEphemeral(true).queue();
        } else if (event.getComponentId().equalsIgnoreCase(appId)) {

            createTicket(appName, "apply", event, event.getMember());
            event.reply("Your ticket was opened").setEphemeral(true).queue();
        }
    }

    private void createTicket(String categoryName, String channelName, Event e, Member mem) {

        Category c = e.getJDA().getCategoriesByName(categoryName, true).get(0);
        List<TextChannel> tclist = new ArrayList<>();
        for (TextChannel tc : c.getTextChannels()) {
            if (tc.getName().equalsIgnoreCase(channelName + "-" + mem.getEffectiveName())) {

                tclist.add(tc);
            }
        }
        if (tclist.size() == 0) {

            c.createTextChannel(channelName + "-" + mem.getEffectiveName()).queue(m -> {

                m.getManager().putMemberPermissionOverride(mem.getIdLong(), List.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();
                m.sendMessage("Your " + channelName +  " ticket is now open. " + mem.getAsMention()).queue();
            });
        } else {
            TextChannel user = tclist.get(0);
            user.sendMessage("Your ticket is open here. " + mem.getAsMention()).queue();
        }
    }
}
