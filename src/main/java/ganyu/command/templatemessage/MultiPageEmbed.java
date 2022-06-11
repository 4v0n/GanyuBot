package ganyu.command.templatemessage;

import ganyu.command.reaction.ReactionCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * @author Aron Navodh Kumarawatta
 * @version 11.06.2022
 */
public class MultiPageEmbed{

    private final String[][] content;
    private String description;
    private int currentPage;
    private Message message;
    private final EmbedBuilder embed;

    public MultiPageEmbed(List<String> content, int itemsPerPage){
        this.content = to2dArray(content.toArray(new String[0]), itemsPerPage);
        this.embed = new EmbedBuilder();
        this.currentPage = 0;
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
        embed.setDescription(buildString());
        return embed.build();
    }

    private String buildString() {
        StringBuilder sb = new StringBuilder();

        String[] strings = content[currentPage];

        sb.append(description).append("\n");
        for (String string : strings) {

            if (string == null) {
                continue;
            }

            sb.append(string).append("\n");
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

    public void setDescription(String description){
        this.description = description;
    }

    @NotNull
    public EmbedBuilder setTitle(@Nullable String title) {
        return embed.setTitle(title);
    }

    @NotNull
    public EmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        return embed.setTitle(title, url);
    }

    public void appendDescription(@NotNull CharSequence description) {
        this.description = this.description + description;
    }

    @NotNull
    public EmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
        return embed.setTimestamp(temporal);
    }

    @NotNull
    public EmbedBuilder setColor(@Nullable Color color) {
        return embed.setColor(color);
    }

    @NotNull
    public EmbedBuilder setColor(int color) {
        return embed.setColor(color);
    }

    @NotNull
    public EmbedBuilder setThumbnail(@Nullable String url) {
        return embed.setThumbnail(url);
    }

    @NotNull
    public EmbedBuilder setImage(@Nullable String url) {
        return embed.setImage(url);
    }

    @NotNull
    public EmbedBuilder setAuthor(@Nullable String name) {
        return embed.setAuthor(name);
    }

    @NotNull
    public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        return embed.setAuthor(name, url);
    }

    @NotNull
    public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        return embed.setAuthor(name, url, iconUrl);
    }

    @NotNull
    public EmbedBuilder setFooter(@Nullable String text) {
        return embed.setFooter(text);
    }

    @NotNull
    public EmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        return embed.setFooter(text, iconUrl);
    }

    @NotNull
    public EmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        return embed.addField(field);
    }

    @NotNull
    public EmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        return embed.addField(name, value, inline);
    }

    @NotNull
    public EmbedBuilder addBlankField(boolean inline) {
        return embed.addBlankField(inline);
    }

    @NotNull
    public EmbedBuilder clearFields() {
        return embed.clearFields();
    }

    @NotNull
    public List<MessageEmbed.Field> getFields() {
        return embed.getFields();
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
