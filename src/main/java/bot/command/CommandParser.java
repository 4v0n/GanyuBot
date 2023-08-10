package bot.command;

import bot.Bot;
import bot.util.ColorScheme;
import bot.util.message.Help;
import bot.util.message.MultiPageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * This class stores and then handles commands
 * When a user sends a command message
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
 public class CommandParser {

    private final HashMap<String, Command> commands;
    private final int layer;
    private final Bot bot;
    private final HashMap<String, String> synonyms;

    public CommandParser(int layer) {
        this.commands = new HashMap<>();
        this.bot = Bot.getInstance();
        this.layer = layer;
        this.synonyms = new HashMap<>();

        this.addCommand(new Command() {
            @Override
            public void run(CommandContext context, List<String> args) {

                HashMap<String, String> commandStrings = new HashMap<>();
                for (Command command : commands.values()){
                    commandStrings.put(command.getCommandWord(), command.getDescription());
                }

                Help.help(bot.getPrefix(context.getGuild()), commandStrings, (MessageReceivedEvent) context.getEvent());
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
            public @NotNull CommandDataImpl getCommandData() {
                return new CommandDataImpl(getCommandWord(), getDescription());
            }

            @Override
            public String[] getSynonyms() {
                return new String[0];
            }
        });

        this.addCommand(new Command() {
                    @Override
                    public void run(CommandContext context, List<String> args) {
                        List<String> array = new ArrayList<>();

                        for (Command command : commands.values()){
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
                        mpe.respond(context);
                    }

                    @Override
                    public @NotNull String getCommandWord() {
                        return "synonyms";
                    }

                    @Override
                    public @NotNull String getDescription() {
                        return "Returns a list of command synonyms. This is only really useful when not using slash commands";
                    }

                    @Override
                    public @NotNull CommandDataImpl getCommandData() {
                        return new CommandDataImpl(getCommandWord(), getDescription());
                    }

                    @Override
                    public String[] getSynonyms() {
                        return new String[]{"aliases"};
                    }
                });
    }

    public void addHelpMessage(String message){
        this.commands.remove("help");

        Command helpCommand = new Command() {
            @Override
            public void run(CommandContext context, List<String> args) {

                HashMap<String, String> commandStrings = new HashMap<>();
                for (Command command : commands.values()){
                    commandStrings.put(command.getCommandWord(), command.getDescription());
                }
                Help.help(bot.getPrefix(context.getGuild()),message, commandStrings, (MessageReceivedEvent) context.getEvent());
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
            public @NotNull CommandDataImpl getCommandData() {
                return new CommandDataImpl(getCommandWord(), getDescription());
            }

            @Override
            public String[] getSynonyms() {
                return new String[0];
            }
        };

        this.commands.put("help", helpCommand);
    }

    public void addCommand(Command command) {
        if (commands.containsKey(command.getCommandWord().toLowerCase())) {
            throw new CommandExistsException(command.getCommandWord());
        }

        for (String synonym : command.getSynonyms()) {
            if (synonyms.containsKey(synonym)) {
                throw new CommandExistsException(command.getCommandWord());
            }
        }

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


        Command command;
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

            command.run(new CommandContext(event), commandWords);
        }
    }

    public void parse(SlashCommandInteractionEvent event) {
        String content = event.getCommandString().substring(1);
        List<String> args = List.of(content.split(" "));

        Command command = commands.get(args.get(layer - 1));
        command.run(new CommandContext(event), null);
    }

    public void addSynonym(String synonym, String original) {
        synonyms.put(synonym, original);
    }

    public HashMap<String, String> getSynonyms() {
        return synonyms;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
