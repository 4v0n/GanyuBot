package blackJack;


import Base.Activity;
import Base.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BlackJackHandler {
    private Bot bot;

    public BlackJackHandler(Bot bot) {
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


        BlackJackCommands command = new BlackJackCommands();
        HashMap<String, String> commands = command.getCommands();


        if (commandWord != null) {
            // if there is a commandWord


            String tags;
            switch (commandWord) {
                case "play":
                    Activity activity = bot.getActivities().get(new String[]{event.getAuthor().getId(), event.getChannel().getId()});
                    if (activity != null) {
                        MessageChannel channel = event.getChannel();
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setDescription("You are already playing a minigame in this channel!");
                        embed.setColor(new Color(255, 0, 0));
                        channel.sendMessageEmbeds(embed.build()).queue();
                    } else startGame(event);

                    break;

                default:
                    MessageChannel channel = event.getChannel();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setDescription("There is no ' " + commandWord + "' command!" +
                            "\nUse the 'help' command to get a list of usable commands." +
                            "\nAll commands are also lower case.");
                    embed.setColor(new Color(255, 0, 0));
                    channel.sendMessageEmbeds(embed.build()).queue();
            }
        } else {
            MessageChannel channel = event.getChannel();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't provided a BlackJack command!" +
                    "\nUse the 'help' command to get a list of usable commands." +
                    "\nAll commands are also lower case.");
            embed.setColor(new Color(255, 0, 0));

            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    private void startGame(MessageReceivedEvent event){
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("The game will be starting soon!");
        embed.setColor(new Color(255, 255, 150));
        channel.sendMessageEmbeds(embed.build()).queue(newMessage -> {
            bot.addActivity(new Game(event, bot, newMessage.getId()));
        });

        System.out.println(bot.getActivities());
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

