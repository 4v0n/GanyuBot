package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.lavaplayer.TrackScheduler;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static bot.command.music.MusicUtil.*;

public class RemoveSongCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        int position = 0;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            if (args.isEmpty()){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You haven't specified which song to remove!");
                sendErrorEmbed(embed, context);
                return;
            }

            if (!checkIsNumber(args.get(0))){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(0) + " isn't a number!");
                sendErrorEmbed(embed, context);
                return;
            }

            position = Integer.parseInt(args.get(0));
        }

        if (event instanceof SlashCommandInteractionEvent) {
            position = Integer.parseInt(((SlashCommandInteractionEvent) event).getOption("position").getAsString());
        }

        position--;

        if (!inVC(context, true)) {
            return;
        }
        if (!playerActive(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        if (!hasPermissions(context, true)) {
            return;
        }
        removeSong(context, position);
    }

    private void removeSong(CommandContext context, int choice) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(context.getGuild()).getScheduler();
        BlockingQueue<AudioTrackInfo> songQueue = scheduler.getSongQueue();
        HashMap<AudioTrackInfo, AudioTrack> songSet = scheduler.getSongSet();


        final List<AudioTrackInfo> trackList = new ArrayList<>(songQueue);

        AudioTrackInfo removedSong;

        try {
            removedSong = trackList.get(choice);

        } catch (Exception e) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("That number is out of bounds!");
            embed.setFooter("There is no song in that position in the song queue");
            sendErrorEmbed(embed, context);
            return;
        }

        trackList.remove(choice);

        songQueue.clear();

        for (AudioTrackInfo trackInfo : trackList) {
            AudioTrack track = songSet.get(trackInfo);
            scheduler.queue(track, context.getMember());
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Removed `" + removedSong.title + "` by `" + removedSong.author + "`");
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "remove";
    }

    @Override
    public @NotNull String getDescription() {
        return "removes a song from the queue by number";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), getDescription());

        OptionData optionData = new OptionData(OptionType.INTEGER, "position",
                "The position of the song you wish to remove", true)
                .setMinValue(1).setMaxValue(Integer.MAX_VALUE);

        commandData.addOptions(optionData);
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"rm"};
    }
}
