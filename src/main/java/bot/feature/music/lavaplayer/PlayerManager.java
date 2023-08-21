package bot.feature.music.lavaplayer;

import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static bot.command.music.MusicUtil.*;

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

    public AudioTrack silentLoad(CommandContext context, String url, Member member) {
        Event event = context.getEvent();
        MusicManager musicManager = null;
        AudioTrack[] foundTrack = {null};

        if (event instanceof MessageReceivedEvent){
            musicManager = this.getMusicManager(((MessageReceivedEvent) event).getGuild());
        }

        if (event instanceof SlashCommandInteractionEvent){
            musicManager = this.getMusicManager(((SlashCommandInteractionEvent) event).getGuild());
        }



        MusicManager finalMusicManager = musicManager;
        try {
            this.audioPlayerManager.loadItemOrdered(finalMusicManager, url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    audioTrack.setUserData(member);
                    finalMusicManager.getScheduler().queue(audioTrack);
                    foundTrack[0] = audioTrack;
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    AudioTrack selectedTrack = audioPlaylist.getSelectedTrack();
                    if (selectedTrack == null){
                        selectedTrack = audioPlaylist.getTracks().get(0);
                    }
                    selectedTrack.setUserData(member);
                    finalMusicManager.getScheduler().queue(selectedTrack);
                    foundTrack[0] = selectedTrack;
                }

                @Override
                public void noMatches() {
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    e.printStackTrace();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

        return foundTrack[0];
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

    public void loadPlaylist(CommandContext context, String url, Member member) {
        Event event = context.getEvent();
        MusicManager musicManager = null;

        if (event instanceof MessageReceivedEvent){
            musicManager = this.getMusicManager(((MessageReceivedEvent) event).getGuild());
        }

        if (event instanceof SlashCommandInteractionEvent){
            musicManager = this.getMusicManager(((SlashCommandInteractionEvent) event).getGuild());
        }

        MusicManager finalMusicManager = musicManager;
        this.audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("This is not a playlist!");
                sendErrorEmbed(embed, context);
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
                context.respondEmbed(embed);
            }

            @Override
            public void noMatches() {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("No close matches were found! \nTry a more precise search query.");
                embed.setFooter("Remember that only youtube songs may be requested");
                sendErrorEmbed(embed, context);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("An error has occurred! \nPerhaps the song / query is invalid");
                embed.setFooter("Remember that only youtube songs may be requested");
                sendErrorEmbed(embed, context);
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
