package ganyu.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ganyu.base.ColorScheme;
import ganyu.music.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ganyu.music.commands.MusicMethods.*;

/**
 * This class manages all active music players
 * <p>
 * Based on MenuDocs' implementation
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
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

    public void loadAndPlay(Event event, String url, Member member) {

        MusicManager musicManager = null;

        if (event instanceof MessageReceivedEvent){
            musicManager = this.getMusicManager(((MessageReceivedEvent) event).getGuild());
        }

        if (event instanceof SlashCommandEvent){
            musicManager = this.getMusicManager(((SlashCommandEvent) event).getGuild());
        }


        MusicManager finalMusicManager = musicManager;
        this.audioPlayerManager.loadItemOrdered(finalMusicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                audioTrack.setUserData(member);
                finalMusicManager.getScheduler().queue(audioTrack);

                ArrayList<AudioTrack> songQueue = new ArrayList<>(finalMusicManager.getScheduler().getSongQueue());
                long position = songQueue.indexOf(audioTrack);

                long timeUntilSong = 0;

                AudioTrack playingTrack = finalMusicManager.getAudioPlayer().getPlayingTrack();
                if (playingTrack != null){
                    timeUntilSong = timeUntilSong + ( playingTrack.getDuration() - playingTrack.getPosition() );
                }

                for (int i = 0; i < position; i++){
                    timeUntilSong = timeUntilSong + songQueue.get(i).getDuration();
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.RESPONSE);

                embed.setAuthor("Added to queue");
                embed.setTitle(audioTrack.getInfo().author + " - " + audioTrack.getInfo().title, audioTrack.getInfo().uri);
                embed.setThumbnail("http://img.youtube.com/vi/" + audioTrack.getInfo().identifier + "/0.jpg");

                if (position > 0) {
                    embed.setDescription("Position in queue: " + position + 1);
                    embed.setFooter("Duration: " + formatTime(audioTrack.getDuration()) + " | Time until song: " + formatTime(timeUntilSong));
                } else {
                    embed.setFooter("Duration: " + formatTime(audioTrack.getDuration()));
                }

                sendEmbed(embed, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                AudioTrack selectedTrack = audioPlaylist.getSelectedTrack();

                if (selectedTrack == null){
                    selectedTrack = audioPlaylist.getTracks().get(0);
                }

                selectedTrack.setUserData(member);

                finalMusicManager.getScheduler().queue(selectedTrack);

                ArrayList<AudioTrack> songQueue = new ArrayList<>(finalMusicManager.getScheduler().getSongQueue());
                long position = songQueue.indexOf(selectedTrack);

                long timeUntilSong = 0;

                AudioTrack playingTrack = finalMusicManager.getAudioPlayer().getPlayingTrack();
                if (playingTrack != null){
                    timeUntilSong = timeUntilSong + ( playingTrack.getDuration() - playingTrack.getPosition() );
                }

                for (int i = 0; i < position; i++){
                    timeUntilSong = timeUntilSong + songQueue.get(i).getDuration();
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.RESPONSE);

                embed.setAuthor("Added to queue");
                embed.setTitle(selectedTrack.getInfo().author + " - " + selectedTrack.getInfo().title, selectedTrack.getInfo().uri);
                embed.setThumbnail("http://img.youtube.com/vi/" + selectedTrack.getInfo().identifier + "/0.jpg");

                if (position > 0) {
                    embed.setDescription("Position in queue: " + position + 1);
                    embed.setFooter("Duration: " + formatTime(selectedTrack.getDuration()) + " | Time until song: " + formatTime(timeUntilSong));
                } else {
                    embed.setFooter("Duration: " + formatTime(selectedTrack.getDuration()));
                }

                sendEmbed(embed, event);
            }

            @Override
            public void noMatches() {

                if (isURL(url)) {
                    String link = "ytsearch:" + url;

                    loadAndPlay(event, link, member);
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("No close matches were found! \nTry a more precise search query.");
                embed.setFooter("Remember that only youtube songs may be requested");
                sendErrorEmbed(embed, event);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("An error has occurred! \nPerhaps the song / query is invalid");
                embed.setFooter("Remember that only youtube songs may be requested");
                sendErrorEmbed(embed, event);
            }
        });
    }

    public void reQueue(Guild guild, String url, Member member) {
        MusicManager musicManager = this.getMusicManager(guild);
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                audioTrack.setUserData(member);
                musicManager.getScheduler().queue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();
                tracks.get(0).setUserData(member);
                musicManager.getScheduler().queue(tracks.get(0));
            }

            @Override
            public void noMatches() {
                if (isURL(url)) {
                    String link = "ytsearch:" + url;

                    reQueue(guild, link, member);
                }
            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void loadPlaylist(Event event, String url, Member member) {
        MusicManager musicManager = null;

        if (event instanceof MessageReceivedEvent){
            musicManager = this.getMusicManager(((MessageReceivedEvent) event).getGuild());
        }

        if (event instanceof SlashCommandEvent){
            musicManager = this.getMusicManager(((SlashCommandEvent) event).getGuild());
        }

        MusicManager finalMusicManager = musicManager;
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("This is not a playlist!");
                sendErrorEmbed(embed, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                long totalTime = 0;

                AudioTrack selectedTrack = audioPlaylist.getSelectedTrack();

                if ((selectedTrack == null)){
                    for (AudioTrack track : audioPlaylist.getTracks()) {
                        finalMusicManager.getScheduler().queue(track);
                        totalTime = totalTime + track.getDuration();
                        track.setUserData(member);
                    }

                } else {
                    ArrayList<AudioTrack> beforeSelectedTrack = new ArrayList<>();
                    boolean reachedSelectedTrack = false;

                    for (AudioTrack track : audioPlaylist.getTracks()){


                        if (reachedSelectedTrack){
                            finalMusicManager.getScheduler().queue(track);

                        } else {
                            reachedSelectedTrack = track.getIdentifier().equals(selectedTrack.getIdentifier());

                            if (reachedSelectedTrack) {
                                finalMusicManager.getScheduler().queue(track);
                            } else {
                                beforeSelectedTrack.add(track);
                            }
                        }

                        totalTime = totalTime + track.getDuration();
                        track.setUserData(member);
                    }

                    for (AudioTrack track : beforeSelectedTrack){
                        finalMusicManager.getScheduler().queue(track);
                    }
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.RESPONSE);
                embed.setAuthor("Queued playlist");
                embed.setTitle("Queued from: " + audioPlaylist.getName(), url);
                embed.setDescription("Queued " + audioPlaylist.getTracks().size() + " songs");

                if (selectedTrack == null) {
                    selectedTrack = audioPlaylist.getTracks().get(0);
                }

                embed.setThumbnail("http://img.youtube.com/vi/" + selectedTrack.getInfo().identifier + "/0.jpg");
                embed.appendDescription("\nStarting from: `" + selectedTrack.getInfo().title + "" +
                        "` by `" + selectedTrack.getInfo().author + "`");
                embed.setFooter("Total duration: " + formatTime(totalTime));
                sendEmbed(embed, event);
            }

            @Override
            public void noMatches() {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("No close matches were found! \nTry a more precise search query.");
                embed.setFooter("Remember that only youtube songs may be requested");
                sendErrorEmbed(embed, event);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("An error has occurred! \nPerhaps the song / query is invalid");
                embed.setFooter("Remember that only youtube songs may be requested");
                sendErrorEmbed(embed, event);
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

}
