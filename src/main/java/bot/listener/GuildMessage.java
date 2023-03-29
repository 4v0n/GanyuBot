package bot.listener;

import bot.activity.Activity;
import bot.Bot;
import bot.feature.root.BaseCommandHandler;
import bot.db.server.ServerData;
import bot.feature.music.MusicParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This command allows the bot to listen to guild messages and
 * process commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class GuildMessage extends ListenerAdapter {
    private final Bot bot;
    private final BaseCommandHandler commandHandler;

    public GuildMessage() {
        this.bot = Bot.getINSTANCE();
        this.commandHandler = BaseCommandHandler.getINSTANCE();
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

        ServerData guildData = bot.getGuildData(event.getGuild());
        if (guildData == null) {
            guildData = new ServerData(event.getGuild());
            bot.addGuildData(guildData);
        }

        Message message = event.getMessage();
        String content = message.getContentRaw().toLowerCase();
        ArrayList<String> words = splitString(content);

        if (words.size() > 0) {
            // allow parsing of music player commands directly
            if (words.get(0).equals( bot.getPrefix(event.getGuild()) + "m" )){
                MusicParser mp = new MusicParser(commandHandler);
                mp.parse(event);
                return;
            }

            Activity activity = bot.getRelevantActivity(event);
            if (activity != null){
                if (activity.getParser().getCommandCenter().containsCommand(words.get(0))){
                    activity.parse(event);
                    return;
                }

                if (words.size() >= 2) {
                    if (activity.getParser().getCommandCenter().containsCommand(words.get(1))) {
                        activity.parse(event);
                        return;
                    }
                }
            }

            if (words.get(0).equals(bot.getPrefix(event.getGuild()))){
                commandHandler.parse(event);
            }
        }
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