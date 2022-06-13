package ganyu.command.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class UsefulMethods {
    public static boolean checkIsInt(String number) {
        try {
            int i = Integer.parseInt(number);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public static boolean checkIsLong(String number) {
        try {
            long i =Long.parseLong(number);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isURL(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
    public static void sendEmbed(EmbedBuilder embed, Event event) {
        if (event instanceof MessageReceivedEvent) {
            ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if (event instanceof SlashCommandEvent) {
            ((SlashCommandEvent) event).replyEmbeds(embed.build()).queue();
        }
    }

    public static void sendEphemeralEmbed(EmbedBuilder embed, Event event) {
        if (event instanceof MessageReceivedEvent) {
            ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if (event instanceof SlashCommandEvent) {
            ((SlashCommandEvent) event).replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }
}
