package ganyu.command.templatemessage;

import ganyu.base.ColorScheme;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This offers a template help message
 *
 * @author Aron Navodh Kumarawatta
 * @version 30.05.2022
 */
public class Help {
    public static void help(String prefix, HashMap<String, String> commands, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        ArrayList<String> commandStrings = new ArrayList<>();

        for (String commandWord : commands.keySet()) {
            String commandDescription = commands.get(commandWord);

            commandDescription = commandDescription.replace("[prefix]", prefix);

            commandStrings.add("- `" + commandWord + "` - " + commandDescription + "\n");
        }


        MultiPageMessage helpMessage = new MultiPageMessage(
                "Command List:",
                "Here is a list of commands: \n" +
                        "The prefix is - ***" + prefix + "*** \n>>> ",
                commandStrings,
                ColorScheme.RESPONSE,
                5
        );

        helpMessage.sendMessage(channel);
    }

    public static void help(String prefix, String notes, HashMap<String, String> commands, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        ArrayList<String> commandStrings = new ArrayList<>();

        for (String commandWord : commands.keySet()) {
            String commandDescription = commands.get(commandWord);

            commandDescription = commandDescription.replace("[prefix]", prefix);

            commandStrings.add("- `" + commandWord + "` - " + commandDescription + "\n");
        }

        notes = notes.replace("[prefix]", prefix);

        MultiPageMessage helpMessage = new MultiPageMessage(
                "Command List:",
                notes + "\n" +
                        "Here is a list of commands: \n" +
                        "The prefix is - ***" + prefix + "*** \n>>> ",
                commandStrings,
                ColorScheme.RESPONSE,
                5
        );

        helpMessage.sendMessage(channel);
    }
}
