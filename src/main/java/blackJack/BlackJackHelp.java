package blackJack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class BlackJackHelp {

    public void help(String prefix, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        StringBuilder outPut = new StringBuilder();
        BlackJackCommands commandClass = new BlackJackCommands();
        HashMap<String, String> commands = commandClass.getCommands();

        Set<String> commandSet = commands.keySet();
        for (String key : commandSet) {
            outPut.append(key).append(" - ").append(commands.get(key)).append(" \n");
        }
        EmbedBuilder embed = new EmbedBuilder();

        embed.setDescription("Here is a list of commands: \n" +
                "The prefix is - ***" + prefix + "***\n>>> " + outPut +
                "\nYou will earn 100 points at the end of each hour." +
                "\nThese will be auto claimed by using any blackjack related command.");
        embed.setColor(new Color(0, 255, 150));

        channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
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