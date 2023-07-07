package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static bot.command.music.MusicUtil.*;

/**
 * This class outlines the remove duplicates command
 * This functions to remove duplicate songs from the song queue
 *
 * @author Aron Navodh Kumarawatta
 * @version 11/10/2022
 */
public class RemoveDuplicatesCommand implements Command {

    /**
     * This is called when the command is given.
     *
     * @param args Arguments passed to the command - arg 0 is Youtube query / link
     */
    @Override
    public void run(CommandContext context, List<String> args) {
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
        removeDuplicates(context);
    }

    /**
     * Removes duplicate songs from song queue in given guild
     */
    private void removeDuplicates(CommandContext context) {
        Guild guild = context.getGuild();
        BlockingQueue<AudioTrack> songQueue = PlayerManager.getInstance().getMusicManager(guild).getScheduler().getSongQueue();
        int initialSize = songQueue.size();
        ArrayList<String> songs = new ArrayList<>();

        AudioTrack playingTrack = PlayerManager.getInstance().getMusicManager(guild).getAudioPlayer().getPlayingTrack();
        songs.add(playingTrack.getIdentifier());

        for (AudioTrack song : songQueue) {
            if (songs.contains(song.getIdentifier())) {
                songQueue.remove(song);

            } else {
                songs.add(song.getIdentifier());
            }
        }

        int finalSize = songQueue.size();

        // send message
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Removed " + (initialSize - finalSize) + " songs from the song queue");
        context.respondEmbed(embed);
    }

    /**
     * Command word
     * @return Command word
     */
    @Override
    public @NotNull String getCommandWord() {
        return "removeduplicates";
    }

    /**
     * Description
     * @return Description of command
     */
    @Override
    public @NotNull String getDescription() {
        return "removes all duplicate songs from the song queue";
    }

    /**
     * Command data
     * @return Command data
     */
    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    /**
     * Synonyms
     * @return Synonyms for command
     */
    @Override
    public String[] getSynonyms() {
        return new String[]{"rd","purge"};
    }
}
