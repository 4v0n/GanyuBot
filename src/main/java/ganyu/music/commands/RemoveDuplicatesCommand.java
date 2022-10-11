package ganyu.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import ganyu.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static ganyu.music.commands.MusicMethods.*;

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
     * @param event The event causing the command
     * @param args Arguments passed to the command - arg 0 is Youtube query / link
     */
    @Override
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;
        Guild guild = null;

        // get required variables
        // to be depricated
        if (event instanceof MessageReceivedEvent) {
            user = ((MessageReceivedEvent) event).getMember();
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            guild = ((MessageReceivedEvent) event).getGuild();

        }

        if (event instanceof SlashCommandEvent) {
            user = ((SlashCommandEvent) event).getMember();
            self = ((SlashCommandEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandEvent) event).getGuild();
        }

        // guard clauses

        if (!user.getVoiceState().inAudioChannel()) {
            // user not in a VC
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a Voice channel!");
            embed.setFooter("Join a voice channel before using this command!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (!self.getVoiceState().inAudioChannel()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The music player is currently inactive!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (inSameVC(user, self)) {
            // user in same VC as bot
            removeDuplicates(guild,event);
        } else {
            // user in different VC from bot
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The music player is currently in a different VC!");
            embed.setFooter("You must be in the same VC to control the music player");
            sendErrorEmbed(embed, event);
        }
    }

    /**
     * Removes duplicate songs from song queue in given guild
     *
     * @param guild Guild the song queue is for
     * @param event The event that requested the command
     */
    private void removeDuplicates(Guild guild, Event event) {
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
        sendEmbed(embed, event);
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
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
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
