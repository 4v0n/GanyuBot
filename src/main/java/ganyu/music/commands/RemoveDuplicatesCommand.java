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

public class RemoveDuplicatesCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;
        Guild guild = null;

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

    private void removeDuplicates(Guild guild, Event event) {
        BlockingQueue<AudioTrack> songQueue = PlayerManager.getInstance().getMusicManager(guild).getScheduler().getSongQueue();
        int initialSize = songQueue.size();
        ArrayList<String> songs = new ArrayList<>();

        for (AudioTrack song : songQueue) {
            if (songs.contains(song.getIdentifier())) {
                songQueue.remove(song);

            } else {
                songs.add(song.getIdentifier());
            }
        }

        int finalSize = songQueue.size();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Removed " + (initialSize - finalSize) + " songs from the song queue");
        sendEmbed(embed, event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "removeduplicates";
    }

    @Override
    public @NotNull String getDescription() {
        return "removes all duplicate songs from the song queue";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"rd","purge"};
    }
}
