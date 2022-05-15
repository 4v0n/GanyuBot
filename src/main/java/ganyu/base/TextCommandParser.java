package ganyu.base;

//import GanyuBot.Gacha.Casino.blackJack.BlackJackHandler;

import ganyu.casino.blackjack.BlackJackHandler;
import ganyu.command.message.Action;
import ganyu.command.message.CommandCenter;
import ganyu.data.GuildData;
import ganyu.image.ImageHandler;
import ganyu.music.MusicParser;
import net.dv8tion.jda.api.EmbedBuilder;
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
 * @version 15.05.2022
 */
public class TextCommandParser extends ListenerAdapter {
    private final Bot bot;
    private final CommandCenter commandCenter;

    public TextCommandParser(Bot bot) {
        this.bot = bot;
        this.commandCenter = new CommandCenter(1);
        buildCommands();
        buildSynonyms();
    }

    /**
     * Run when message recieved
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // exit if the message is sent by a bot
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        // load guild data / create new if it doesn't exist
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
        commandCenter.addCommand("copy", "Copies what you type", this::copyCommand);

        commandCenter.addCommand("images", "Image commands. Use `>g images help` for more info",
                (event, args) -> {
                    System.out.println("running");
                    ImageHandler image = new ImageHandler(bot);
                    image.parse(event);
                });

        commandCenter.addCommand("blackjack", "Blackjack minigame. use `>g blackjack help` for more info",
                (event, args) -> {
                    BlackJackHandler blackjack = new BlackJackHandler(bot);
                    blackjack.parse(event);
                });

        commandCenter.addCommand("musicplayer", "Joins your voice channel and starts the music player",
                new Action() {
                    @Override
                    public void run(MessageReceivedEvent event, List<String> args) {
                        MusicParser mp = new MusicParser();
                        mp.parse(event);
                    }
                });

        commandCenter.addCommand("test", "test", new Action() {
            @Override
            public void run(MessageReceivedEvent event, List<String> args) {
                System.out.println(event.getGuild().getAudioManager().getConnectedChannel().getMembers());
                System.out.println(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size());
            }
        });
    }

    private void copyCommand(MessageReceivedEvent event, List<String> list) {
        Message message = event.getMessage();
        String content = message.getContentRaw();

        /*
        well all of this was useless i guess.

        ArrayList<String> words = new ArrayList<>();
        Scanner tokenizer = new Scanner(content);


        while (tokenizer.hasNext()){
            words.add(tokenizer.next());
        }

        words.remove(0);
        words.remove(0);

        String copiedMessage = Arrays.toString(new ArrayList[]{words});

        MessageChannel channel = event.getChannel();
        channel.sendMessage(copiedMessage.substring(1,copiedMessage.length()-1)).queue();
        */

        MessageChannel channel = event.getChannel();
        if (content.length() > 8) {
            content = content.substring(8);
            //content = content.substring(0, content.length()-1);
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