package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.music.DiscoveredVidId;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static bot.command.music.MusicUtil.*;

public class PlayListCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Event event = context.getEvent();
        String link = null;

        if (event instanceof MessageReceivedEvent) {
            link = String.join(" ", args);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            link = ((SlashCommandInteractionEvent) event).getOption("url").getAsString();
        }

        if (link == null || link.isBlank() || link.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a link / search query for a song!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!isURL(link)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("That is not a link!\nYou need to provide a url for this command to work!");
            embed.setColor(ColorScheme.ERROR);
            sendErrorEmbed(embed, context);
        }

        URI uri = null;
        try {
            uri = new URI(link);
        } catch (URISyntaxException ignored) {
            // will always be ignored due to guard clause
        }

        if (!playerActive(context, false)) {
            if (isVCEmpty(context, true)) {
                joinVoiceChannel(context);

                if (uri.getAuthority().equals("open.spotify.com")) {
                    queueSpotifyList(context, link);
                    return;
                }

                queuePlaylist(context, link);
            }
            return;
        }
        if (!inVC(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }

        if (uri.getAuthority().equals("open.spotify.com")) {
            queueSpotifyList(context, link);
            return;
        }
        queuePlaylist(context, link);
    }

    private void joinVoiceChannel(CommandContext context) {
        Member user = context.getMember();
        Member self = context.getSelfMember();

        AudioChannel audioChannel = user.getVoiceState().getChannel();
        self.getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`");
        embed.setColor(ColorScheme.RESPONSE);

        context.getMessageChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void queuePlaylist(CommandContext context, String link) {
        PlayerManager.getInstance().loadPlaylist(context, link, context.getMember());
    }

    private void queueSpotifyList(CommandContext context, String link) {
        EmbedBuilder warning = new EmbedBuilder();
        warning.setAuthor("Queuing playlist");
        warning.setDescription("**The loading process will only include songs from the preview embed.**\nSongs found from spotify links may not be accurate or may not even be found!");
        warning.setFooter("This process may take a while.\nSongs queued during the process will be added in the middle of the playlist.");
        warning.setColor(ColorScheme.INFO);


        SpotifyManager spotifyManager = SpotifyManager.getINSTANCE();

        URI uri = null;
        try {
            uri = new URI(link);
        } catch (URISyntaxException ignored) {
        }

        EmbedBuilder finalEmbed = new EmbedBuilder();
        ArrayList<String> trackStrings = new ArrayList<>();
        HashMap<String, String> spotifyIds = new HashMap<>();

        try {
            if (uri.getPath().startsWith("/album/")) {
                Album album = spotifyManager.getAlbum(link);
                finalEmbed.setTitle("Queued from: " + album.getName(), link);
                finalEmbed.setThumbnail(album.getImages()[0].getUrl());

                warning.setTitle("Queueing from: " + album.getName(), link);
                warning.setThumbnail(album.getImages()[0].getUrl());
                context.respondEmbed(warning);

                for (TrackSimplified track : album.getTracks().getItems()) {
                    String query = (buildArtistString(track.getArtists()) + "- " + track.getName());
                    trackStrings.add(query);
                    spotifyIds.put(query, track.getId());
                }
            }
            if (uri.getPath().startsWith("/playlist")) {
                Playlist playlist = spotifyManager.getPlaylist(link);
                finalEmbed.setTitle("Queued from: " + playlist.getName(), link);
                finalEmbed.setThumbnail(playlist.getImages()[0].getUrl());

                warning.setTitle("Queueing from: " + playlist.getName(), link);
                warning.setThumbnail(playlist.getImages()[0].getUrl());
                context.respondEmbed(warning);

                for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {
                    Track track = (Track) playlistTrack.getTrack();
                    String query = (buildArtistString(track.getArtists()) + "- " + track.getName());
                    trackStrings.add(query);
                    spotifyIds.put(query, track.getId());
                }
            }
        } catch (NullPointerException e) {
            EmbedBuilder error = new EmbedBuilder();
            error.setColor(ColorScheme.ERROR);
            error.setTitle("Error");
            error.setDescription("Something went wrong!" +
                    "\nIt is possible that the link does not direct to a playlist, or the playlist could be set to private.");
            return;
        }

        //handle in thread
        Thread thread = new Thread(() -> {
            ArrayList<AudioTrack> queuedSongs = new ArrayList<>();
            for (String song : trackStrings) {
                AudioTrack audioTrack;
                String ytId = DiscoveredVidId.getYoutubeIdFromSpotifyId(spotifyIds.get(song));

                if (ytId != null) {
                    YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager();
                    audioTrack = ((AudioTrack) yasm.loadTrackWithVideoId(ytId, false));
                    audioTrack.setUserData(context.getMember());
                    PlayerManager.getInstance().getMusicManager(context.getGuild()).getScheduler().queue(audioTrack);
                } else {
                    audioTrack = PlayerManager.getInstance().silentLoad(context, "ytsearch:" + song, context.getMember());
                    if (audioTrack == null) {
                        continue;
                    }
                    DiscoveredVidId discovered = new DiscoveredVidId(spotifyIds.get(song), audioTrack.getIdentifier(), audioTrack.getInfo().title);
                    Bot.getInstance().getDatastore().save(discovered);
                }
                queuedSongs.add(audioTrack);
            }

            long totalTime = 0;
            for (AudioTrack track : queuedSongs) {
                totalTime = totalTime + track.getDuration();
            }

            AudioTrackInfo trackInfo = queuedSongs.get(0).getInfo();
            finalEmbed.setColor(ColorScheme.RESPONSE);
            finalEmbed.setAuthor("Queued playlist");
            finalEmbed.setDescription("Queued " + queuedSongs.size() + " songs out of " + trackStrings.size() +
                    "\nStarting from: `" + trackInfo.title + "` by `" + trackInfo.author + "`");
            finalEmbed.setFooter("Total duration: " + formatTime(totalTime));
            context.getMessageChannel().sendMessageEmbeds(finalEmbed.build()).queue();
        });
        thread.start();
    }

    private String buildArtistString(ArtistSimplified[] artists) {
        StringBuilder sb = new StringBuilder();
        for (ArtistSimplified artist : artists) {
            sb.append(artist.getName());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public @NotNull String getCommandWord() {
        return "playlist";
    }

    @Override
    public @NotNull String getDescription() {
        return "Adds a playlist of songs (spotify/youtube) to the queue" +
                " Usage: `[prefix] mp playlist [link]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), getDescription());
        commandData.addOption(OptionType.STRING, "url", "The URL to the playlist", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"pl"};
    }
}
