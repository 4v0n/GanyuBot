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

import static bot.command.music.MusicUtil.inSameVC;
import static bot.command.music.MusicUtil.sendErrorEmbed;

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
        Member user = context.getMember();
        Member self = context.getSelfMember();
        Guild guild = context.getGuild();

        // guard clauses

        if (!user.getVoiceState().inAudioChannel()) {
            // user not in a VC
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a Voice channel!");
            embed.setFooter("Join a voice channel before using this command!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!self.getVoiceState().inAudioChannel()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The music player is currently inactive!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (inSameVC(user, self)) {
            // user in same VC as bot
            removeDuplicates(guild,context);
        } else {
            // user in different VC from bot
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The music player is currently in a different VC!");
            embed.setFooter("You must be in the same VC to control the music player");
            sendErrorEmbed(embed, context);
        }
    }

    /**
     * Removes duplicate songs from song queue in given guild
     *
     * @param guild Guild the song queue is for
     */
    private void removeDuplicates(Guild guild, CommandContext context) {
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
