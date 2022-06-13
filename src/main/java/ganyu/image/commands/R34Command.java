package ganyu.image.commands;

import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.entities.BoardImage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class R34Command implements Command {

    @Override
    public void run(Event event, List<String> args) {

        if (event instanceof MessageReceivedEvent){
            if (args.size() > 0) {
                String words = join((ArrayList<String>) args, " ");
                searchRule34(words, event);
            } else {
                noTagsError((MessageReceivedEvent) event);
            }
            return;
        }

        if (event instanceof SlashCommandEvent){
            String tags = ((SlashCommandEvent) event).getOption("tags").getAsString();
            searchRule34(tags, event);
        }
    }
    private void noTagsError(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("You haven't provided any tags!");
        embed.setColor(ColorScheme.ERROR);
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    private void searchRule34(String tags, Event event) {

        boolean nsfwChannel = false;

        if (event instanceof MessageReceivedEvent){
            nsfwChannel = ((MessageReceivedEvent) event).getTextChannel().isNSFW();
        } else if (event instanceof SlashCommandEvent){
            nsfwChannel = ((SlashCommandEvent) event).getTextChannel().isNSFW();
        }

        if (nsfwChannel) {
            DefaultImageBoards.RULE34.search(50, tags).async(rule34Images -> {

                boolean illegal = false; // whether illegal content has been found

                ArrayList<BoardImage> images = new ArrayList<>();
                for (BoardImage image : rule34Images) {

                    // prevent underage content from being shown
                    if (image.getTags().contains("loli")) {
                        illegal = true;
                        continue;
                    }

                    images.add(image);
                }

                if (images.size() > 0) {
                    if (images.size() == 1) {
                        displayImage(tags, images.get(0), event);
                    } else {
                        Random random = new Random();
                        int choice = random.nextInt(images.size() - 1);

                        if (images.get(choice).getTags().contains("loli")) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("no wtf");
                            embed.setImage("https://cdn.7tv.app/emote/60bd66d67c2d79e1a9285551/4x");
                            embed.setColor(ColorScheme.ERROR);

                            if (event instanceof MessageReceivedEvent) {
                                ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).reference(((MessageReceivedEvent) event).getMessage()).queue();
                            } else {
                                ((SlashCommandEvent) event).replyEmbeds(embed.build()).setEphemeral(true).queue();
                            }

                            return;
                        }

                        displayImage(tags, images.get(choice), event);
                    }

                } else if (illegal) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("No");
                    embed.setFooter("The 'loli' tag was found");
                    embed.setColor(ColorScheme.ERROR);

                    if (event instanceof MessageReceivedEvent) {
                        ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).reference(((MessageReceivedEvent) event).getMessage()).queue();
                    } else {
                        ((SlashCommandEvent) event).replyEmbeds(embed.build()).setEphemeral(true).queue();
                    }

                } else {
                    noImagesError(event);
                }


            });
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("This isn't an nsfw channel");
            embed.setImage("https://cdn.frankerfacez.com/emoticon/555265/4");
            embed.setFooter("Use this command in an NSFW channel or use the SFW command instead.");
            embed.setColor(ColorScheme.ERROR);

            if (event instanceof MessageReceivedEvent) {
                ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            } else {
                assert event instanceof SlashCommandEvent;
                ((SlashCommandEvent) event).replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
        }
    }

    private void noImagesError(Event event){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("No images were found for these tags");
        embed.setImage("https://cdn.betterttv.net/emote/5e8c3a008fb1ca5cde58723f/3x");
        embed.setFooter("Perhaps the tags may be incorrectly spelt or formatted.");
        embed.setColor(ColorScheme.ERROR);

        if (event instanceof MessageReceivedEvent){
            ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (event instanceof SlashCommandEvent){
            ((SlashCommandEvent) event).replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }

    private void displayImage(String tags, BoardImage image, Event event) {


        EmbedBuilder embed = new EmbedBuilder();
        String url = image.getURL();


        if (url.endsWith(".png")) {
            String[] tempURL = url.split("/");
            tempURL[3] = "samples";
            tempURL[5] = ("sample_" + tempURL[5].substring(0, tempURL[5].length() - 3) + "jpg");

            String newUrl = join(tempURL, "/");
            newUrl = newUrl.substring(0, newUrl.length() - 1);

            embed.setImage(newUrl);
        } else {
            embed.setImage(url);
        }

        embed.setAuthor(tags);
        embed.setTitle("Original file", url);

        embed.setColor(ColorScheme.RESPONSE);

        if (event instanceof MessageReceivedEvent){
            ((MessageReceivedEvent) event).getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (event instanceof SlashCommandEvent){
            ((SlashCommandEvent) event).replyEmbeds(embed.build()).queue();
        }
    }

    private String join(String[] array, String splitter) {
        StringBuilder newString = new StringBuilder();
        for (String word : array) {
            newString.append(word).append(splitter);
        }
        return newString.toString();
    }

    private String join(ArrayList<String> array, String splitter) {
        StringBuilder newString = new StringBuilder();
        for (String word : array) {
            newString.append(word).append(splitter);
        }
        return newString.toString();
    }

    @Override
    public @NotNull String getCommandWord() {
        return "nsfw";
    }

    @Override
    public @NotNull String getDescription() {
        return "Searches Rule34 for an image with the supplied tags. Usage: `[prefix] images sfw [tag1] [tag2] ...` Only works in an NSFW channel.";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        CommandData commandData = new CommandData(getCommandWord(), "Searches Rule34 for an image");
        commandData.addOption(OptionType.STRING, "tags", "Enter search tags here. (Separate by space)", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
