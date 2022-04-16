package CommandStructure;

import Base.Bot;
import Base.ColorScheme;
import Base.Main;
import botFunctions.Help;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandCenter {

    private final HashMap<String, Action> commandList;
    private final HashMap<String, String> commandDescriptions;
    private final int layer;
    private final Bot bot;
    private HashMap<String, String> synonyms;

    public CommandCenter(int layer) {
        this.commandList = new HashMap<>();
        this.commandDescriptions = new HashMap<>();
        this.bot = Main.getBotData();
        this.layer = layer;
        this.synonyms = new HashMap<>();

        this.addCommand("help", "Returns a list of commands",
                (event, args) -> {
                    Help help = new Help();
                    help.help(bot.getPrefix(), this.getCommandDescriptions(), event);
                });

        this.addCommand("synonyms", "Returns a list of command synonyms",
                (event, args) -> {
                    String string = "";
                    for (String synonym : synonyms.keySet()){
                        string = (string + synonym + " = " + synonyms.get(synonym) + "\n");
                    }
                    MessageChannel channel = event.getChannel();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setDescription(string);
                    embed.setColor(ColorScheme.RESPONSE);
                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                });
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

    private ArrayList<String> addToBegining(String newItem, ArrayList<String> list){

        ArrayList<String> tempList = new ArrayList<>();
        tempList.add(newItem);
        tempList.addAll(list);

        return tempList;
    }

    private void replaceSynonyms(ArrayList<String> words, HashMap<String, String> synonyms){
        int index = 0;
        for (String word : words){
            if (synonyms.containsKey(word)){
                words.set(index, synonyms.get(word));
            }
            index ++;
        }
    }

    public void parse(MessageReceivedEvent event) {
        ArrayList<String> commandWords = splitString(event.getMessage().getContentRaw());

        if (!commandWords.get(0).equals(bot.getPrefix())) {
                commandWords = addToBegining(bot.getPrefix(), commandWords);
        }

        replaceSynonyms(commandWords, synonyms);

        System.out.println(commandWords);

        if (commandWords.size() > 1) {
            Action action = commandList.get(commandWords.get(layer));
            if (action != null) {
                // remove everything and including the commandWord
                for (int i = 0; i < layer + 1; i++){
                    if (!commandWords.isEmpty()) {
                        commandWords.remove(0);
                    }
                }

                System.out.println(commandWords);

                action.run(event, commandWords);

            } else {
                MessageChannel channel = event.getChannel();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("There is no ' " + commandWords.get(layer) + "' command!" +
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

    public void addSynonym(String synonym, String original){
        synonyms.put(synonym, original);
    }
}