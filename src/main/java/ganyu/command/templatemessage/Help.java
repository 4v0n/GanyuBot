package ganyu.command.templatemessage;

import ganyu.base.ColorScheme;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This offers a template help message
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class Help {
    public static void help(String prefix, HashMap<String, String> commands, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        ArrayList<String> commandStrings = new ArrayList<>();

        for (String commandWord : commands.keySet()) {
            String commandDescription = commands.get(commandWord);

            commandDescription = commandDescription.replace("[prefix]", prefix);

            commandStrings.add("- `" + commandWord + "` - " + commandDescription);
        }


        MultiPageEmbed helpMessage = new MultiPageEmbed(commandStrings, 5);
        helpMessage.setDescription(
                "Here is a list of commands: \n" +
                "The prefix is - ***" + prefix + "*** \n>>> ");
        helpMessage.setAuthor("Help");
        helpMessage.setTitle("Command list:");
        helpMessage.setColor(ColorScheme.RESPONSE);

        helpMessage.sendMessage(channel);
    }

    public static void help(String prefix, HashMap<String, String> commands, SlashCommandEvent event) {
        MessageChannel channel = event.getChannel();

        ArrayList<String> commandStrings = new ArrayList<>();

        for (String commandWord : commands.keySet()) {
            String commandDescription = commands.get(commandWord);

            commandDescription = commandDescription.replace("[prefix]", prefix);

            commandStrings.add("- `" + commandWord + "` - " + commandDescription);
        }


        MultiPageEmbed helpMessage = new MultiPageEmbed(commandStrings, 5);
        helpMessage.setDescription(
                "Here is a list of commands: \n" +
                        "The prefix is - ***" + prefix + "*** \n>>> ");
        helpMessage.setAuthor("Help");
        helpMessage.setTitle("Command list:");
        helpMessage.setColor(ColorScheme.RESPONSE);

        helpMessage.replyTo(event);
    }

    public static void help(String prefix, String notes, HashMap<String, String> commands, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        ArrayList<String> commandStrings = new ArrayList<>();

        for (String commandWord : commands.keySet()) {
            String commandDescription = commands.get(commandWord);

            commandDescription = commandDescription.replace("[prefix]", prefix);

            commandStrings.add("- `" + commandWord + "` - " + commandDescription);
        }

        notes = notes.replace("[prefix]", prefix);

        MultiPageEmbed helpMessage = new MultiPageEmbed(commandStrings, 5);
        helpMessage.setDescription(notes + "\n" +
                "Here is a list of commands: \n" +
                "The prefix is - ***" + prefix + "*** \n>>> ");
        helpMessage.setAuthor("Help");
        helpMessage.setTitle("Command list:");
        helpMessage.setColor(ColorScheme.RESPONSE);

        helpMessage.sendMessage(channel);
    }

    public static void help(String prefix, String notes, HashMap<String, String> commands, SlashCommandEvent event) {
        MessageChannel channel = event.getChannel();

        ArrayList<String> commandStrings = new ArrayList<>();

        for (String commandWord : commands.keySet()) {
            String commandDescription = commands.get(commandWord);

            commandDescription = commandDescription.replace("[prefix]", prefix);

            commandStrings.add("- `" + commandWord + "` - " + commandDescription);
        }

        notes = notes.replace("[prefix]", prefix);

        MultiPageEmbed helpMessage = new MultiPageEmbed(commandStrings, 5);
        helpMessage.setDescription(notes + "\n" +
                "Here is a list of commands: \n" +
                "The prefix is - ***" + prefix + "*** \n>>> ");
        helpMessage.setAuthor("Help");
        helpMessage.setTitle("Command list:");
        helpMessage.setColor(ColorScheme.RESPONSE);

        helpMessage.replyTo(event);
    }
}
