package Base;

//import blackJack.BlackJackHandler;

import blackJack.BlackJackHandler;
import botFunctions.Copy;
import botFunctions.Help;
import imageComponent.imageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser extends ListenerAdapter {
    private Bot bot;

    public Parser(Bot bot){
        this.bot = bot;

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        if (words.size() > 0) {
            if (words.get(0).equals(bot.getPrefix())) {
                parseCommand(words, event);
            }
        }
    }

    public String getPrefix() {
        return bot.getPrefix();
    }

    private ArrayList<String> splitString(String string) {
        ArrayList<String> stringArray = new ArrayList<>();
        Scanner tokenizer = new Scanner(string);

        while (tokenizer.hasNext()) {
            stringArray.add(tokenizer.next());
        }
        return stringArray;
    }

    private void parseCommand(ArrayList<String> args, MessageReceivedEvent event) {
        String commandWord = null;
        args.remove(0); // remove the prefix so it is not dealt with
        if (args.size() > 0){
            commandWord = args.get(0);
        }

        Command command = new Command();
        HashMap<String, String> commands = command.getCommands();

        MessageChannel channel = event.getChannel();

        // if there is a command word:
        if (commandWord != null) {
            switch (commandWord) {
                case "help":
                    Help help = new Help();
                    help.help(bot.getPrefix(), commands, event);
                    break;
                case "copy":
                    Copy copy = new Copy();
                    copy.copy(event);
                    break;
                case "images":
                    imageHandler image = new imageHandler();
                    image.parse(event);
                    break;
                case "blackjack":
                    BlackJackHandler blackjack = new BlackJackHandler(bot);
                    blackjack.parse(event);
                    break;
                default:
                    if ((bot.getActivities().containsKey(event.getAuthor().getId() + event.getChannel().getId()))) {
                        Activity activity = bot.getActivities().get(event.getAuthor().getId() + event.getChannel().getId());
                        if (activity.getParser().getCommandList().contains(commandWord)) {
                            activity.parse(event, bot);
                        }
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setDescription("There is no ' " + commandWord + "' command!" +
                                "\nUse the 'help' command to get a list of usable commands." +
                                "\nAll commands are also lower case.");
                        embed.setColor(new Color(255, 0, 0));
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    }
            }
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't provided a command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(new Color(255, 0, 0));
            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
        }
    }
}