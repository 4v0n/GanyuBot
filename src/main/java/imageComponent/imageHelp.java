package imageComponent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class imageHelp {
    public void help(String prefix, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        ArrayList<String> words = splitString(content);

        System.out.println(words.size());
        StringBuilder outPut = new StringBuilder();
        imageCommands commandClass = new imageCommands();
        HashMap<String, String> commands = commandClass.getCommands();

        Set<String> commandSet = commands.keySet();
        for (String key : commandSet) {
            outPut.append(key).append(" - ").append(commands.get(key)).append(" \n");
        }
        EmbedBuilder embed = new EmbedBuilder();

        embed.setDescription("Here is a list of commands: \n" +
                "The prefix is - ***" + prefix + "***\n>>> " + outPut);
        embed.setColor(new Color(0, 255, 150));

        channel.sendMessageEmbeds(embed.build()).queue();
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
