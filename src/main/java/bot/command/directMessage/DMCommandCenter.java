package bot.command.directMessage;

import bot.Bot;
import bot.command.CommandExistsException;
import bot.util.ColorScheme;
import bot.util.message.Help;
import bot.util.message.MultiPageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * This class identifies and executes DM commands issued by the user.
 */
public class DMCommandCenter {
    private final HashMap<String, DMCommand> commands;
    private final int layer;
    private final Bot bot;
    private final HashMap<String, String> synonyms;

    public DMCommandCenter(int layer) {
        this.commands = new HashMap<>();
        this.bot = Bot.getInstance();
        this.layer = layer;
        this.synonyms = new HashMap<>();

        this.addCommand(new DMCommand() {
            @Override
            public void run(MessageReceivedEvent event, List<String> args) {
                HashMap<String, String> commandStrings = new HashMap<>();
                for (DMCommand command : commands.values()){
                    commandStrings.put(command.getCommandWord(), command.getDescription());
                }
                Help.help(bot.getGlobalPrefix(), commandStrings, event);
            }

            @Override
            public @NotNull String getCommandWord() {
                return "help";
            }

            @Override
            public @NotNull String getDescription() {
                return "Returns a list of commands and info about this command / command set";
            }

            @Override
            public String[] getSynonyms() {
                return new String[0];
            }
        });

        this.addCommand(new DMCommand() {
            @Override
            public void run(MessageReceivedEvent event, List<String> args) {
                List<String> array = new ArrayList<>();

                for (DMCommand command : commands.values()){
                    for (String synonym : command.getSynonyms()){
                        array.add(
                                synonym + " = " + command.getCommandWord()
                        );
                    }
                }

                MultiPageEmbed mpe = new MultiPageEmbed(array, 5);
                mpe.setTitle("Command synonyms");
                mpe.setDescription("These synonyms equate to these commands:");
                mpe.setColor(ColorScheme.RESPONSE);
                mpe.sendMessage(event.getChannel());
            }

            @Override
            public @NotNull String getCommandWord() {
                return "synonyms";
            }

            @Override
            public @NotNull String getDescription() {
                return "Returns a list of command synonyms.";
            }

            @Override
            public String[] getSynonyms() {
                return new String[]{"aliases"};
            }
        });
    }

    /**
     * Add a message to this command branch's help message.
     * @param message
     */
    public void addHelpMessage(String message){
        this.commands.remove("help");

        DMCommand helpCommand = new DMCommand() {
            @Override
            public void run(MessageReceivedEvent event, List<String> args) {
                HashMap<String, String> commandStrings = new HashMap<>();
                for (DMCommand command : commands.values()){
                    commandStrings.put(command.getCommandWord(), command.getDescription());
                }
                Help.help(bot.getGlobalPrefix(),message, commandStrings, event);
            }

            @Override
            public @NotNull String getCommandWord() {
                return "help";
            }

            @Override
            public @NotNull String getDescription() {
                return "Returns a list of commands and info about this command / command set";
            }

            @Override
            public String[] getSynonyms() {
                return new String[0];
            }
        };

        this.commands.put("help", helpCommand);
    }

    public void addCommand(DMCommand command) {
        //check if a command with the command name/alias exists
        if (commands.containsKey(command.getCommandWord().toLowerCase())) {
            throw new CommandExistsException(command.getCommandWord());
        }
        for (String synonym : command.getSynonyms()) {
            if (synonyms.containsKey(synonym)) {
                throw new CommandExistsException(command.getCommandWord());
            }
        }

        //add command
        commands.put(command.getCommandWord().toLowerCase(), command);
    }

    public boolean containsCommand(String commandWord) {
        return commands.containsKey(commandWord) || synonyms.containsKey(commandWord);
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

    /**
     * Looks through a tokenized command string and replaces
     * synonyms (aliases) with their respective command name
     * @param words Tokenized command string
     * @param synonyms Set of synonyms
     */
    private void replaceSynonyms(ArrayList<String> words, HashMap<String, String> synonyms) {
        int index = 0;
        for (String word : words) {
            if (synonyms.containsKey(word.toLowerCase())) {
                words.set(index, synonyms.get(word.toLowerCase()));
            }
            index++;
        }
    }

    /**
     * Look for an issued command and execute it.
     * @param event The event which is issuing the command
     */
    public void parse(MessageReceivedEvent event) {
        ArrayList<String> commandWords = splitString(event.getMessage().getContentRaw());

        if (!commandWords.get(0).toLowerCase().equals(bot.getGlobalPrefix())) {
            commandWords = addToBeginning(bot.getGlobalPrefix(), commandWords);
        }

        replaceSynonyms(commandWords, synonyms);


        DMCommand command;
        try {
            command = commands.get(commandWords.get(layer).toLowerCase());
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


        if (command == null) {
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
            command.run(event, commandWords);
        }
    }

    public void addSynonym(String synonym, String original) {
        synonyms.put(synonym, original);
    }

    public HashMap<String, String> getSynonyms() {
        return synonyms;
    }

    public HashMap<String, DMCommand> getCommands() {
        return commands;
    }
}
