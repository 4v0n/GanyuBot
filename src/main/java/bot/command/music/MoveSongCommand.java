package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
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

public class MoveSongCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Event event = context.getEvent();
        int oldPos = 0;
        int newPos = 1;

        if (event instanceof MessageReceivedEvent) {
            if (args.size() < 2) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("This command requires 2 arguments!");
                sendErrorEmbed(embed, context);
                return;
            }

            if (!checkIsNumber(args.get(0))) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(0) + " isn't a number!");
                sendErrorEmbed(embed, context);
                return;
            }

            if (!checkIsNumber(args.get(1))) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription(args.get(1) + " isn't a number!");
                sendErrorEmbed(embed, context);
                return;
            }

            oldPos = Integer.parseInt(args.get(0));
            newPos = Integer.parseInt(args.get(1));
        }

        if (event instanceof SlashCommandInteractionEvent) {
            oldPos = Integer.parseInt(((SlashCommandInteractionEvent) event).getOption("old_position").getAsString());
            newPos = Integer.parseInt(((SlashCommandInteractionEvent) event).getOption("new_position").getAsString());
        }

        if (!inVC(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        if (!hasPermissions(context, true)) {
            return;
        }

        moveSong(oldPos, newPos, context);
    }

    private void moveSong(int oldPos, int newPos, CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        BlockingQueue<AudioTrackInfo> songQueue = musicManager.getScheduler().getSongQueue();

        if (songQueue.size() < 2){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There aren't enough songs to move around!");
            sendErrorEmbed(embed, context);
            return;
        }

        oldPos--;
        newPos--;

        final List<AudioTrackInfo> trackList = new ArrayList<>(songQueue);

        AudioTrackInfo movedSong;

        try {
            movedSong = trackList.get(oldPos);

            trackList.remove(oldPos);
            trackList.add(newPos, movedSong);

        } catch (Exception e) {

            return;
        }

        songQueue.clear();

        HashMap<AudioTrackInfo, AudioTrack> songSet = musicManager.getScheduler().getSongSet();
        for (AudioTrackInfo trackInfo : trackList) {
            AudioTrack track = songSet.get(trackInfo);
            musicManager.getScheduler().queue(track, context.getMember());
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Moved `" + movedSong.title + "` to position `" + (newPos + 1) + "`");
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "move";
    }

    @Override
    public @NotNull String getDescription() {
        return "moves a track from one position to another. Usage: `[prefix] mp move [oldpos] [newpos]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "moves a track from one position to another");

        OptionData oldPosOptionData = new OptionData(OptionType.INTEGER, "old_position", "The position of the song you want to move", true).setMinValue(2);
        OptionData newPosOptionData = new OptionData(OptionType.INTEGER, "new_position", "The position you want to move the song to", true).setMinValue(1);

        commandData.addOptions(oldPosOptionData, newPosOptionData);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"mv"};
    }
}
