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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public void loadAndPlay(MessageReceivedEvent event, String url, Member member) {
        MusicManager musicManager = this.getMusicManager(event.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                audioTrack.setUserData(member);
                musicManager.getScheduler().queue(audioTrack);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.RESPONSE);
                embed.setDescription("Queued song: `" + audioTrack.getInfo().title + "` - `" + audioTrack.getInfo().author + "`");
                embed.setFooter("Duration: " + formatTime(audioTrack.getDuration()));
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                AudioTrack selectedTrack = audioPlaylist.getSelectedTrack();

                if (selectedTrack == null){
                    selectedTrack = audioPlaylist.getTracks().get(0);
                }

                selectedTrack.setUserData(member);

                musicManager.getScheduler().queue(selectedTrack);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.RESPONSE);
                embed.setDescription("Queued song: `" + selectedTrack.getInfo().title + "` - `" + selectedTrack.getInfo().author + "`");
                embed.setFooter("Duration: " + formatTime(selectedTrack.getDuration()));
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
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
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("An error has occurred! \nPerhaps the song / query is invalid");
                embed.setFooter("Remember that only youtube songs may be requested");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
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

    public void loadPlaylist(MessageReceivedEvent event, String url, Member member) {
        MusicManager musicManager = this.getMusicManager(event.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("This is not a playlist!");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                long totalTime = 0;

                AudioTrack selectedTrack = audioPlaylist.getSelectedTrack();

                if ((selectedTrack == null)){
                    for (AudioTrack track : audioPlaylist.getTracks()) {
                        musicManager.getScheduler().queue(track);
                        totalTime = totalTime + track.getDuration();
                        track.setUserData(member);
                    }

                } else {
                    ArrayList<AudioTrack> beforeSelectedTrack = new ArrayList<>();
                    boolean reachedSelectedTrack = false;

                    for (AudioTrack track : audioPlaylist.getTracks()){


                        if (reachedSelectedTrack){
                            musicManager.getScheduler().queue(track);

                        } else {
                            reachedSelectedTrack = track.getIdentifier().equals(selectedTrack.getIdentifier());

                            if (reachedSelectedTrack) {
                                musicManager.getScheduler().queue(track);
                            } else {
                                beforeSelectedTrack.add(track);
                            }
                        }

                        totalTime = totalTime + track.getDuration();
                        track.setUserData(member);
                    }

                    for (AudioTrack track : beforeSelectedTrack){
                        musicManager.getScheduler().queue(track);
                    }
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.RESPONSE);
                embed.setDescription("Queued `" + audioPlaylist.getTracks().size() + "` songs from `" + audioPlaylist.getName() + "`");
                embed.setFooter("Duration: " + formatTime(totalTime));
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void noMatches() {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("No close matches were found! \nTry a more precise search query.");
                embed.setFooter("Remember that only youtube songs may be requested");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("An error has occurred! \nPerhaps the song / query is invalid");
                embed.setFooter("Remember that only youtube songs may be requested");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
        });
    }


    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    private String formatTime(long duration) {

        Duration time = Duration.ofMillis(duration);
        long seconds = time.toSeconds();

        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;

        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    private boolean isURL(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
