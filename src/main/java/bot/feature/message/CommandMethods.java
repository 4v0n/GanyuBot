package bot.feature.message;

import bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static net.dv8tion.jda.api.entities.Message.*;

public class CommandMethods {
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

    public static boolean isCommand(Message msg) {
        String content = msg.getContentRaw();
        return content.contains(Bot.getINSTANCE().getPrefix(msg.getGuild())) || content.contains(">g");
    }

    public static boolean hasImage(Message msg) {
        List<Attachment> attachments = msg.getAttachments();
        for (Attachment attachment : attachments) {
            if (attachment.isImage()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasVideo(Message msg) {
        List<Attachment> attachments = msg.getAttachments();
        for (Attachment attachment : attachments) {
            if (attachment.isVideo()) {
                return true;
            }
        }
        return false;
    }

    public static boolean linksToDomain(Message msg, String domain) {
        return msg.getContentRaw().contains("www." + domain);
    }

    public static OptionMapping getOption(List<OptionMapping> options, String optionName) {
        for (OptionMapping option : options) {
            if (option.getName().equals(optionName)) {
                return option;
            }
        }
        return null;
    }
}
