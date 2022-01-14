package botFunctions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class Copy {
    public void copy(MessageReceivedEvent event){

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
            embed.setColor(new Color(255, 0, 0));
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
