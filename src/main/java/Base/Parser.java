package Base;

//import Casino.blackJack.BlackJackHandler;

import Casino.Blackjack.BlackJackHandler;
import CommandStructure.Action;
import CommandStructure.CommandCenter;
import WOU.Handler;
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
    private final Bot bot;
    private final CommandCenter commandCenter;

    public Parser(Bot bot) {
        this.bot = bot;
        this.commandCenter = new CommandCenter();
        buildCommands();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);


        if (words.contains("bj")) {
            int index = words.indexOf("bj");
            words.set(index, "blackjack");
        }

        if (words.size() > 0) {
            if (words.get(0).equals(bot.getPrefix())) {
                commandCenter.parse(event);
            } else if ((bot.getRelevantActivity(event) != null) && bot.getRelevantActivity(event).getParser().getCommandList().contains(words.get(0))) {
                bot.getRelevantActivity(event).parse(event, bot);
            }
        }
    }

    private void replaceSynonyms(ArrayList<String> words){
        HashMap<String, String> synonyms = new HashMap<>();

        for (String word : words){

        }
    }

    private void buildCommands() {
        commandCenter.addCommand("help", "Returns a list of commands",
                new Action() {
                    @Override
                    public void run(MessageReceivedEvent event) {
                        Help help = new Help();
                        help.help(bot.getPrefix(), commandCenter.getCommandDescriptions(), event);
                    }
                });

        commandCenter.addCommand("copy", "Copies what you type",
                new Action() {
                    @Override
                    public void run(MessageReceivedEvent event) {
                        Copy copy = new Copy();
                        copy.copy(event);
                    }
                });

        commandCenter.addCommand("images", "Image commands. Use `>g help images` for more info",
                new Action() {
                    @Override
                    public void run(MessageReceivedEvent event) {
                        imageHandler image = new imageHandler();
                        image.parse(event);
                    }
                });

        commandCenter.addCommand("blackjack", "Blackjack minigame. use `>g help blackjack` for more info",
                new Action() {
                    @Override
                    public void run(MessageReceivedEvent event) {
                        BlackJackHandler blackjack = new BlackJackHandler(bot);
                        blackjack.parse(event);
                    }
                });

        commandCenter.addCommand("wou", "My shitty 'world of the undead' game that I made for a uni assignment.",
                new Action() {
                    @Override
                    public void run(MessageReceivedEvent event) {
                        Handler wou = new Handler(bot);
                        wou.parse(event);
                    }
                });
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

    private void parseAdminCommands(ArrayList<String> args, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        args.remove(0);

        if (bot.getAdmins().contains(event.getAuthor().getId()) && args.size() >= 2) {
            switch (args.get(0)) {
                case "stopAutoSave":
                    String choice = args.get(1);
                    if (choice.equals("true")) {
                        bot.setAutosaveActive(true);
                    } else if (choice.equals("false")) {
                        bot.setAutosaveActive(false);
                    } else {

                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setDescription("You must enter either `true` or `false`");
                        embed.setColor(new Color(255, 0, 0));
                        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
                    }
                    break;

                case "addCredits":
                    String value = args.get(1);

                    break;

                case "setCredits":
                    break;

                case "resetCredits:":
                    break;

                default:
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setDescription("You haven't provided a command!" +
                            "\nUse the 'help' command to get a list of usable commands." +
                            "\nAll commands are also lower case.");
                    embed.setColor(new Color(255, 0, 0));
            }
        }
    }
}