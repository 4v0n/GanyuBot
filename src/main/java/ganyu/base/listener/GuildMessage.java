package ganyu.base.listener;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.casino.blackjack.BlackJackHandler;
import ganyu.command.message.CommandCenter;
import ganyu.data.ServerData;
import ganyu.image.ImageHandler;
import ganyu.music.MusicParser;
import ganyu.settings.SettingsParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This command allows the bot to listen to guild messages and
 * process commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 29.05.2022
 */
public class GuildMessage extends ListenerAdapter {
    private final Bot bot;
    private final CommandCenter commandCenter;

    public GuildMessage() {
        this.bot = Bot.getINSTANCE();
        this.commandCenter = new CommandCenter(1);
        buildCommands();
        buildSynonyms();
    }

    /**
     * Run when message received
     *
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // exit if the message is sent by a bot
        if (event.getAuthor().isBot()) return;

        try {
            event.getGuild();
        } catch (Exception e) {
            return;
        }

        bot.loadGuildData(event.getGuild());

        ServerData guildData = bot.getGuildData().get(event.getGuild());
        if (guildData == null) {
            guildData = new ServerData(event.getGuild());
            bot.addGuildData(guildData);
        }

        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        if (words.size() > 0) {
            if (words.get(0).equals(bot.getPrefix(event.getGuild()))) {
                if (words.size() > 1) {
                    if (bot.getRelevantActivity(event) != null && bot.getRelevantActivity(event).getParser().getCommandCenter().containsCommand(words.get(1))) {
                        bot.getRelevantActivity(event).parse(event);
                    } else {
                        commandCenter.parse(event);
                    }
                } else {
                    commandCenter.parse(event);
                }
            } else if ((bot.getRelevantActivity(event) != null) && bot.getRelevantActivity(event).getParser().getCommandCenter().containsCommand(words.get(0))) {
                bot.getRelevantActivity(event).parse(event);
            }
        }
    }

    private void buildCommands() {
        commandCenter.addCommand("copy", "Copies what you type", this::copyCommand);

        commandCenter.addCommand("images", "Image commands. Use `[prefix] images help` for more info",
                (event, args) -> {
                    System.out.println("running");
                    ImageHandler image = new ImageHandler();
                    image.parse(event);
                });

        commandCenter.addCommand("blackjack", "Blackjack mini-game commands. use `[prefix] blackjack help` for more info",
                (event, args) -> {
                    BlackJackHandler blackjack = new BlackJackHandler();
                    blackjack.parse(event);
                });

        commandCenter.addCommand("musicplayer", "Music bot commands. use `[prefix] mp help` for more info",
                (event, args) -> {
                    MusicParser mp = new MusicParser();
                    mp.parse(event);
                });


        commandCenter.addCommand("settings", "Allows to access the bot settings", (event, args) -> {
            Member member = event.getMember();

            boolean hasPermission = false;

            for (Permission permission : member.getPermissions()) {
                if (permission.getName().equals("Administrator")) hasPermission = true;
            }

            if (!hasPermission) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Insufficient permissions!");
                embed.setDescription("You need to have the `Administrator` permission to use this set of commands");
                embed.setColor(ColorScheme.ERROR);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            SettingsParser settingsParser = new SettingsParser(bot.getGuildData().get(event.getGuild()));
            settingsParser.parse(event);
        });

        commandCenter.addCommand("info", "Shows info about the bot", (event, args) -> {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Info:");
            embed.setDescription("A bot programmed by <@195929905857429504> using Java 11.0.15 and JDA 5.0.0-alpha.3");
            embed.setColor(ColorScheme.RESPONSE);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        });
    }

    private void copyCommand(MessageReceivedEvent event, List<String> list) {
        Message message = event.getMessage();
        String content = message.getContentRaw();

        MessageChannel channel = event.getChannel();
        if (content.length() > 8) {
            content = content.substring(8);
            channel.sendMessage(content).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't typed anything to copy!");
            embed.setColor(ColorScheme.ERROR);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    private void buildSynonyms() {
        commandCenter.addSynonym("bj", "blackjack");
        commandCenter.addSynonym("mp", "musicplayer");
    }

    private ArrayList<String> splitString(String string) {
        ArrayList<String> stringArray = new ArrayList<>();
        Scanner tokenizer = new Scanner(string);

        while (tokenizer.hasNext()) {
            stringArray.add(tokenizer.next());
        }
        return stringArray;
    }
}