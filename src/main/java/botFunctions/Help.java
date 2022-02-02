package botFunctions;

import blackJack.BlackJackHelp;
import imageComponent.imageHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Help {
    public void help(String prefix, HashMap<String, String> commands, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        if (words.size() <= 2) {
            StringBuilder outPut = new StringBuilder();
            Set<String> commandSet = commands.keySet();
            for (String key : commandSet) {
                outPut.append(key).append(" - ").append(commands.get(key)).append(" \n");
            }

            EmbedBuilder embed = new EmbedBuilder();

            embed.setDescription("Here is a list of commands: \n" +
                    "The prefix is - ***" + prefix + "***\n>>> " + outPut);
            embed.setColor(new Color(0, 255, 150));

            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();

        } else if (words.get(2).equals("images")) {
            imageHelp help = new imageHelp();
            help.help(prefix, event);
        }else if (words.get(2).equals("blackjack")) {
            BlackJackHelp help = new BlackJackHelp();
            help.help(prefix, event);
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("There is no help for the '" + words.get(2) + "' command!" +
                    "\nThere may be no further help for the command or the command may not exist." +
                    "\nAll commands are also lower case.");
            embed.setColor(new Color(255, 0, 0));
            channel.sendMessageEmbeds(embed.build()).reference(event.getMessage()).queue();
        }
    }

    private ArrayList<String> splitString(String string){
        ArrayList<String> stringArray = new ArrayList<>();
        Scanner tokenizer = new Scanner(string);

        while (tokenizer.hasNext()){
            stringArray.add(tokenizer.next());
        }
        return stringArray;
    }
}
