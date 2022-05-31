package ganyu.command.templatemessage;

import ganyu.command.reaction.ReactionCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

/**
 * @author Aron Kumarawatta
 * @version 29.05.2022
 */
public class MultiPageMessage {

    private final String[][] content;
    private final String title;
    private final String text;
    private int currentPage;
    private final Color color;
    private Message message;

    public MultiPageMessage(String title, String text, String[] content, Color color, int itemsPerPage) {
        this.title = title;
        this.text = text;
        this.color = color;
        this.currentPage = 0;

        this.content = to2dArray(content, itemsPerPage);
    }

    public MultiPageMessage(String title, String text, List<String> content, Color color, int itemsPerPage) {
        this.title = title;
        this.text = text;
        this.color = color;
        this.currentPage = 0;
        this.content = to2dArray(content.toArray(new String[0]), itemsPerPage);
    }

    private String[][] to2dArray(String[] content, int itemsPerPage) {
        int pages = (int) Math.ceil((double) (content.length) / itemsPerPage);

        String[][] array2d = new String[pages][itemsPerPage];

        int i = 0;
        int a = 0;
        String[] subArray = new String[itemsPerPage];

        for (String item : content) {

            subArray[i] = item;
            i++;

            if (i >= itemsPerPage) {
                array2d[a] = subArray;
                a++;
                i = 0;
                subArray = new String[itemsPerPage];
                continue;
            }
            array2d[a] = subArray;
        }

        return array2d;
    }

    private MessageEmbed buildEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setColor(color);
        embed.setDescription(buildString());

        return embed.build();
    }

    private String buildString() {
        StringBuilder sb = new StringBuilder();

        String[] strings = content[currentPage];

        sb.append(text).append("\n");
        for (String string : strings) {

            if (string == null) {
                continue;
            }

            sb.append(string);
        }

        if (content.length > 1) {
            sb.append("\nPage: ").append(currentPage + 1).append("/").append(content.length);
        }

        return sb.toString();
    }

    private void nextPage() {
        currentPage++;

        if (currentPage > content.length - 1) {
            currentPage = 0;
        }
        editMessage();
    }

    private void prevPage() {
        currentPage--;

        if (currentPage < 0) {
            currentPage = content.length - 1;
        }
        editMessage();
    }

    public void sendMessage(MessageChannel channel) {
        channel.sendMessageEmbeds(buildEmbed()).queue(this::setMessage);
    }

    private void editMessage() {
        message.editMessageEmbeds(buildEmbed()).queue();
    }


    private void setMessage(Message message) {
        this.message = message;

        if (content.length > 1) {
            ReactionListener reactionListener = new ReactionListener(message);
            reactionListener.activate(1);
        }
    }

    public Message getMessage() {
        return message;
    }

    private class ReactionListener extends ReactionCommandHandler {

        public ReactionListener(Message message) {
            super(message);
        }

        @Override
        public void buildCommands() {
            getCommandCenter().addCommand("⬅", event -> prevPage());

            getCommandCenter().addCommand("➡", event -> nextPage());
        }
    }
}
