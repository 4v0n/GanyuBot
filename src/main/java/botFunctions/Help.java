package botFunctions;

import Base.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

        StringBuilder outPut = new StringBuilder();
        Set<String> commandSet = commands.keySet();
        for (String key : commandSet) {
            outPut.append(key).append(" - ").append(commands.get(key)).append(" \n");
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setDescription("Here is a list of commands: \n" +
                "The prefix is - ***" + prefix + "***\n>>> " + outPut);
        embed.setColor(ColorScheme.RESPONSE);

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
