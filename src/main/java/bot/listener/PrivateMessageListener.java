package bot.listener;

import bot.Bot;
import bot.feature.root.BaseDMCommandHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Scanner;

public class PrivateMessageListener extends ListenerAdapter {
    private final Bot bot;
    private final BaseDMCommandHandler commandHandler;

    public PrivateMessageListener() {
        this.bot = Bot.getInstance();
        this.commandHandler = BaseDMCommandHandler.getInstance();
    }

    /**
     * Run when message received
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // exit if the message is sent by a bot
        if (event.getAuthor().isBot()) return;
        if (!event.getChannelType().equals(ChannelType.PRIVATE)) return;

        Message message = event.getMessage();
        String content = message.getContentRaw().toLowerCase();
        ArrayList<String> words = splitString(content);

        if (words.size() > 0) {
            if (words.get(0).equals(bot.getGlobalPrefix())) {
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
