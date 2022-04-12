package CommandStructure;

import Base.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class CommandCenter {

    private final HashMap<String, Action> commandList;
    private final HashMap<String, String> commandDescriptions;

    public CommandCenter() {
        this.commandList = new HashMap<>();
        this.commandDescriptions = new HashMap<>();
    }

    public void addCommand(String commandName, String description, Action action) {
        commandList.put(commandName, action);
        commandDescriptions.put(commandName, description);
    }

    public HashMap<String, Action> getCommandList() {
        return commandList;
    }

    public HashMap<String, String> getCommandDescriptions() {
        return commandDescriptions;
    }

    public boolean containsCommand(String commandWord) {
        return commandList.containsKey(commandWord);
    }

    private String[] splitString(String string) {
        Scanner tokenizer = new Scanner(string);
        String[] stringArray = new String[string.split(" ").length];

        int index = 0;
        while (tokenizer.hasNext()) {
            stringArray[index] = (tokenizer.next());
            index++;
        }
        return stringArray;
    }

    public void parse(MessageReceivedEvent event) {
        String[] commandWords = splitString(event.getMessage().getContentRaw());
        System.out.println(Arrays.toString(commandWords));

        if (commandWords.length > 1) {
            Action action = commandList.get(commandWords[1]);
            if (action != null) {
                action.run(event);

            } else {
                MessageChannel channel = event.getChannel();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("There is no ' " + commandWords[0] + "' command!" +
                        "\nUse the 'help' command to get a list of usable commands." +
                        "\nAll commands are also lower case.");
                embed.setColor(ColorScheme.ERROR);
                channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
            }
        } else {
            MessageChannel channel = event.getChannel();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't provided a command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
        }
    }
}
