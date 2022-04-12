package WOU;

import Base.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Handler {
    private final Bot bot;

    public Handler(Bot bot) {
        this.bot = bot;
    }

    public void parse(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);


        //remove the prefix and old command word and get the new command word
        String commandWord = null;
        words.remove(0);
        words.remove(0);
        if (words.size() > 0) {
            commandWord = words.get(0);
        }

        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();

        if (commandWord != null) {
            // if there is a commandWord
            String tags;
            switch (commandWord) {
                case "play":
                    startGame(event);
                    break;

                case "help":

                    break;

                default:
                    embed.setDescription("There is no ' " + commandWord + "' command!" +
                            "\nUse the 'help' command to get a list of usable commands." +
                            "\nAll commands are also lower case.");
                    embed.setColor(new Color(255, 0, 0));
                    channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
            }
        } else {
            embed.setDescription("You haven't provided a world of undead command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(new Color(255, 0, 0));

            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
        }
    }

    private void startGame(MessageReceivedEvent event){
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("The game will be starting soon!");
        embed.setColor(new Color(255, 255, 150));
        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue(newMessage -> {
            bot.addActivity(new Game(event, new Parser(bot), newMessage, bot));
        });

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
