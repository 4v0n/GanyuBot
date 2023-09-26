package bot.feature.music.lavaplayer;

import bot.feature.music.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static bot.command.music.MusicUtil.isURL;

/**
 * This class manages all active music players
 * <p>
 * Based on MenuDocs' implementation
 *
 * @author Aron Navodh Kumarawatta
 * @version 28.03.2023
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

    public MusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) -> {
            final MusicManager musicManager = new MusicManager(this.audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(musicManager.getSentHandler());
            return musicManager;
        });
    }

    public void removeMusicManager(Guild guild) {
        musicManagers.remove(guild);
    }

    public AudioTrack loadTrack(String identifier) {
        final AudioTrack[] loadedTrack = {null};
        try {
            this.audioPlayerManager.loadItem(identifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    loadedTrack[0] = audioTrack;
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    loadedTrack[0] = audioPlaylist.getSelectedTrack();

                    if (loadedTrack[0] == null){
                        loadedTrack[0] = audioPlaylist.getTracks().get(0);
                    }
                }

                @Override
                public void noMatches() {
                    if (isURL(identifier)) {
                        String newIdentifier = "ytsearch:" + identifier;
                        loadedTrack[0] = loadTrack(newIdentifier);
                    }
                }

                @Override
                public void loadFailed(FriendlyException e) {
                }
            }).get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return loadedTrack[0];
    }

    public AudioPlaylist loadPlayList(String identifier) {
        final AudioPlaylist[] playlist = {null};
        try {
            this.audioPlayerManager.loadItem(identifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    //ignore
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    playlist[0] = audioPlaylist;
                }

                @Override
                public void noMatches() {
                    //ignore
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    //ignore
                }
            }).get();
        } catch (InterruptedException | ExecutionException ignored) {
        }
        return playlist[0];
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

}
