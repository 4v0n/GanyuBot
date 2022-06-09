package ganyu.command.message;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.command.CommandExistsException;
import ganyu.command.templatemessage.Help;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

/**
 * This class stores and then handles commands
 * When a user sends a command message
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class CommandCenter {

    private final HashMap<String, Action> commandList;
    private final HashMap<String, String> commandDescriptions;
    private final int layer;
    private final Bot bot;
    private final HashMap<String, String> synonyms;

    public CommandCenter(int layer) {
        this.commandList = new HashMap<>();
        this.commandDescriptions = new HashMap<>();
        this.bot = Bot.getINSTANCE();
        this.layer = layer;
        this.synonyms = new HashMap<>();

        this.addCommand("help", "Returns a list of commands and info about this command / command set",
                (event, args) -> {
                    Help.help(bot.getPrefix(event.getGuild()), this.getCommandDescriptions(), event);
                });

        this.addCommand("synonyms", "Returns a list of command synonyms",
                (event, args) -> {
                    StringBuilder string = new StringBuilder();
                    for (String synonym : synonyms.keySet()) {
                        string.append(synonym)
                                .append(" = ")
                                .append(synonyms.get(synonym))
                                .append("\n");
                    }
                    MessageChannel channel = event.getChannel();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setDescription(string.toString());
                    embed.setColor(ColorScheme.RESPONSE);
                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                });
    }

    public void addHelpMessage(String message){
        this.commandList.remove("help");
        this.commandDescriptions.remove("help");

        Action action = new Action() {
            @Override
            public void run(MessageReceivedEvent event, List<String> args) {
                Help.help(bot.getPrefix(event.getGuild()),message, commandDescriptions, event);
            }
        };

        this.commandList.put("help", action);
        this.commandDescriptions.put("help", "Returns a list of commands and info about this command / command set");
    }

    public void addCommand(String commandName, String description, Action action) {

        if (commandList.containsKey(commandName)) {
            throw new CommandExistsException(commandName);
        }

        commandList.put(commandName.toLowerCase(), action);
        commandDescriptions.put(commandName.toLowerCase(), description);
    }

    public HashMap<String, String> getCommandDescriptions() {
        return commandDescriptions;
    }

    public boolean containsCommand(String commandWord) {
        return commandList.containsKey(commandWord) || synonyms.containsKey(commandWord);
    }

    private ArrayList<String> splitString(String string) {
        Scanner tokenizer = new Scanner(string);
        ArrayList<String> list = new ArrayList<>();

        while (tokenizer.hasNext()) {
            list.add(tokenizer.next());
        }

        return list;
    }

    private ArrayList<String> addToBeginning(String newItem, ArrayList<String> list) {

        ArrayList<String> tempList = new ArrayList<>();
        tempList.add(newItem);
        tempList.addAll(list);

        return tempList;
    }

    private void replaceSynonyms(ArrayList<String> words, HashMap<String, String> synonyms) {
        int index = 0;
        for (String word : words) {
            if (synonyms.containsKey(word.toLowerCase())) {
                words.set(index, synonyms.get(word.toLowerCase()));
            }
            index++;
        }
    }

    public void parse(MessageReceivedEvent event) {
        ArrayList<String> commandWords = splitString(event.getMessage().getContentRaw());

        if (!commandWords.get(0).toLowerCase().equals(bot.getPrefix(event.getGuild()))) {
            commandWords = addToBeginning(bot.getPrefix(event.getGuild()), commandWords);
        }

        replaceSynonyms(commandWords, synonyms);


        Action action;
        try {
            action = commandList.get(commandWords.get(layer).toLowerCase());

        } catch (Exception e) {
            MessageChannel channel = event.getChannel();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't provided a command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
            return;
        }


        if (action == null) {
            MessageChannel channel = event.getChannel();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("There is no ' " + commandWords.get(layer) + "' command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();

        } else {
            // remove everything and including the commandWord
            for (int i = 0; i < layer + 1; i++) {
                if (!commandWords.isEmpty()) {
                    commandWords.remove(0);
                }
            }

            action.run(event, commandWords);

        }
    }


    public void addSynonym(String synonym, String original) {
        synonyms.put(synonym, original);
    }
}
