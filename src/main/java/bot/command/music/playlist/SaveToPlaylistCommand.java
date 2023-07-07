package bot.command.music.playlist;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.command.music.MusicUtil;
import bot.db.music.Playlist;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static bot.command.music.MusicUtil.sendErrorEmbed;

public class SaveToPlaylistCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        String name = null;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            name = String.join(" ", args);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            name = ((SlashCommandInteractionEvent) event).getOption("name").getAsString();
        }

        if (name == null || name.isBlank() || name.isEmpty()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a name for the new playlist!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!MusicUtil.playerActive(context, true)) {
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack playingTrack = musicManager.getAudioPlayer().getPlayingTrack();
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();

        if (playingTrack == null || songQueue.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is nothing in the queue to turn into a playlist!");
            sendErrorEmbed(embed, context);
            return;
        }

        ArrayList<AudioTrack> songs = new ArrayList<>();
        songs.add(playingTrack);
        songs.addAll(songQueue);
        createNewPlaylistFromQueue(name, context.getAuthor(), songs);
        context.respondMessage("done");
    }

    public void createNewPlaylistFromQueue(String name, User owner, ArrayList<AudioTrack> songs) {
        Playlist playlist = new Playlist(owner, name, songs);
        Bot.getINSTANCE().getDatastore().save(playlist);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "savenewlist";
    }

    @Override
    public @NotNull String getDescription() {
        return "Saves the currently playing and in queue songs to a new playlist." +
                " Usage: `[prefix] mp l savenewlist [name of playlist]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "Creates a new playlist from the currently playing song and queued songs");
        commandData.addOption(OptionType.STRING, "name", "The name of the new playlist", true);
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"snl"};
    }
}
