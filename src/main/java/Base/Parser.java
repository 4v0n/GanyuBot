package Base;

//import Casino.blackJack.BlackJackHandler;

import Casino.Blackjack.BlackJackHandler;
import CommandStructure.CommandCenter;
import Database.GuildData;
import botFunctions.Copy;
import imageComponent.imageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser extends ListenerAdapter {
    private final Bot bot;
    private final CommandCenter commandCenter;
    private final CommandCenter adminCommandCenter;

    public Parser(Bot bot) {
        this.bot = bot;
        this.commandCenter = new CommandCenter(1);
        this.adminCommandCenter = new CommandCenter(2);
        buildCommands();
        buildSynonyms();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        GuildData guildData = bot.getGuildData().get(event.getGuild().getId());
        if ((guildData) == null) {
            guildData = new GuildData(event.getGuild().getId());
            bot.addGuildData(event.getGuild().getId(), guildData);
        }

        if (words.size() > 0) {
            if (words.get(0).equals(bot.getPrefix())) {
                if (words.size() > 1) {
                    if (bot.getRelevantActivity(event) != null && bot.getRelevantActivity(event).getParser().getCommandCenter().containsCommand(words.get(1))) {
                        bot.getRelevantActivity(event).parse(event, bot);
                    } else {
                        commandCenter.parse(event);
                    }
                } else {
                    commandCenter.parse(event);
                }
            } else if ((bot.getRelevantActivity(event) != null) && bot.getRelevantActivity(event).getParser().getCommandCenter().containsCommand(words.get(0))) {
                bot.getRelevantActivity(event).parse(event, bot);
            }
        }
    }

    private void buildCommands() {
        commandCenter.addCommand("copy", "Copies what you type",
                (event, args) -> {
                    Copy copy = new Copy();
                    copy.copy(event);
                });

        commandCenter.addCommand("images", "Image commands. Use `>g images help` for more info",
                (event, args) -> {
                    System.out.println("running");
                    imageHandler image = new imageHandler(bot);
                    image.parse(event);
                });

        commandCenter.addCommand("blackjack", "Blackjack minigame. use `>g blackjack help` for more info",
                (event, args) -> {
                    BlackJackHandler blackjack = new BlackJackHandler(bot);
                    blackjack.parse(event);
                });

        commandCenter.addCommand("stopAutoSave", "Stops the bot from autosaving",
                (event, args) -> {

                });
    }

    private void buildSynonyms() {
        commandCenter.addSynonym("bj", "blackjack");
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