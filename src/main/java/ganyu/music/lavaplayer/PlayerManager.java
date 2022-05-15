package ganyu.music.lavaplayer;

import ganyu.music.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

/**
 * This class manages all active music players
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
 */
public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final HashMap<Long, MusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public MusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) -> {
            final MusicManager musicManager = new MusicManager(this.audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(musicManager.getSentHandler());
            return musicManager;
        });
    }

    public void loadAndPlay(MessageReceivedEvent event, String url){
        MessageChannel channel = event.getChannel();

        MusicManager musicManager = this.getMusicManager(event.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.getScheduler().queue(audioTrack);
                channel.sendMessage("Queued song: `")
                        .append(audioTrack.getInfo().title)
                        .append("`by`")
                        .append(audioTrack.getInfo().author)
                        .append("`")
                        .queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();
                musicManager.getScheduler().queue(tracks.get(0));
                channel.sendMessage("Queued song: `")
                        .append(tracks.get(0).getInfo().title)
                        .append("`by`")
                        .append(tracks.get(0).getInfo().author)
                        .append("`")
                        .queue();
            }

            @Override
            public void noMatches() {
                channel.sendMessage("No accurate matches found").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
            }
        });
    }

    public void loadAndPlay(Guild guild, String url){
        MusicManager musicManager = this.getMusicManager(guild);
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.getScheduler().queue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();
                musicManager.getScheduler().queue(tracks.get(0));
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public static PlayerManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
