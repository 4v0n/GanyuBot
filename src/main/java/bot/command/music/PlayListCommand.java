package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.BatchQueueJob;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.lavaplayer.TrackScheduler;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
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
import java.util.List;

import static bot.command.music.MusicUtil.*;

public class PlayListCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Event event = context.getEvent();
        String identifier = null;

        if (event instanceof MessageReceivedEvent) {
            identifier = String.join(" ", args);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            identifier = ((SlashCommandInteractionEvent) event).getOption("url").getAsString();
        }

        if (identifier == null || identifier.isBlank() || identifier.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a link / search query for a song!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!isURL(identifier)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("That is not a link!\nYou need to provide a url for this command to work!");
            embed.setColor(ColorScheme.ERROR);
            sendErrorEmbed(embed, context);
        }

        URI uri = null;
        try {
            uri = new URI(identifier);
        } catch (URISyntaxException ignored) {
            // will always be ignored due to guard clause
        }

        if (!playerActive(context, false)) {
            if (isVCEmpty(context, true)) {
                joinVoiceChannel(context);

                if (uri.getAuthority().equals("open.spotify.com")) {
                    loadSpotifyList(context, identifier);
                    return;
                }

                loadPlaylist(context, identifier);
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
            loadSpotifyList(context, identifier);
            return;
        }
        loadPlaylist(context, identifier);
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

    private void loadSpotifyList(CommandContext context, String identifier) {
        Event event = context.getEvent();
        EmbedBuilder warning = new EmbedBuilder();
        warning.setAuthor("Queuing playlist");
        warning.setDescription("**The loading process will only include songs from the preview embed.**" +
                "\nSongs found from spotify links may not be accurate or may not even be found!");
        warning.setColor(ColorScheme.INFO);

        SpotifyManager spotifyManager = SpotifyManager.getInstance(context.getAuthor());
        URI uri = null;
        try {
            uri = new URI(identifier);
        } catch (URISyntaxException ignored) {
        }

        try {
            if (uri.getPath().startsWith("/album/")) {
                Album album = spotifyManager.getAlbum(identifier);
                warning.setTitle("Queueing from: " + album.getName(), identifier);
                warning.setThumbnail(album.getImages()[0].getUrl());

                if (event instanceof SlashCommandInteractionEvent) {
                    SlashCommandInteractionEvent evt = (SlashCommandInteractionEvent) event;
                    evt.getHook().sendMessageEmbeds(warning.build()).queue(message -> loadSpotifyAlbum(album, context, message, identifier));
                } else {
                    context.getMessageChannel().sendMessageEmbeds(warning.build()).queue(message -> loadSpotifyAlbum(album, context, message, identifier));
                }
            }
            if (uri.getPath().startsWith("/playlist")) {
                Playlist playlist = spotifyManager.getPlaylist(identifier);
                warning.setTitle("Queueing from: " + playlist.getName(), identifier);
                warning.setThumbnail(playlist.getImages()[0].getUrl());

                if (event instanceof SlashCommandInteractionEvent) {
                    SlashCommandInteractionEvent evt = (SlashCommandInteractionEvent) event;
                    evt.getHook().sendMessageEmbeds(warning.build()).queue(message -> loadSpotifyPlaylist(playlist, context, message, identifier));
                } else {
                    context.getMessageChannel().sendMessageEmbeds(warning.build()).queue(message -> loadSpotifyPlaylist(playlist, context, message, identifier));
                }
            }
        } catch (Exception e) {
            EmbedBuilder error = new EmbedBuilder();
            error.setColor(ColorScheme.ERROR);
            error.setTitle("Error");
            error.setDescription("Something went wrong!" +
                    "\nIt is possible that the link does not point to a playlist, or the playlist could be set to private.");
            error.setFooter("Linking your spotify account will let you access your private playlists.");
            context.respondEmbed(error);
        }
    }

    private void loadSpotifyAlbum(Album album, CommandContext context, Message message, String identifier) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(context.getGuild()).getScheduler();
        BatchQueueJob job = scheduler.queue(album, context.getMember());

        long totalTime = 0;
        for (TrackSimplified track : album.getTracks().getItems()) {
            totalTime = totalTime + track.getDurationMs();
        }

        ArrayList<AudioTrackInfo> tracks = job.getTrackInfoInOrder();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setAuthor("Finished Queueing playlist");
        embed.setTitle("Queued from: " + album.getName(), identifier);
        embed.setDescription("Queued " + tracks.size() + " out of " + album.getTracks().getItems().length + " songs");
        embed.setThumbnail(album.getImages()[0].getUrl());
        embed.appendDescription("\nStarting from: `" + tracks.get(0).title + "" +
                "` by `" + tracks.get(0).author + "`");
        embed.setFooter("Total duration: ≈" + formatTime(totalTime));
        message.getChannel().sendMessageEmbeds(embed.build()).setMessageReference(message).queue();
    }

    private void loadSpotifyPlaylist(Playlist playlist, CommandContext context, Message message, String identifier) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(context.getGuild()).getScheduler();
        BatchQueueJob job = scheduler.queue(playlist, context.getMember());

        long totalTime = 0;
        for (PlaylistTrack track : playlist.getTracks().getItems()) {
            totalTime = totalTime + track.getTrack().getDurationMs();
        }

        ArrayList<AudioTrackInfo> tracks = job.getTrackInfoInOrder();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setAuthor("Finished Queueing playlist");
        embed.setTitle("Queued from: " + playlist.getName(), identifier);
        embed.setDescription("Queued " + tracks.size() + " out of " + playlist.getTracks().getItems().length + " songs");
        embed.setThumbnail(playlist.getImages()[0].getUrl());
        embed.appendDescription("\nStarting from: `" + tracks.get(0).title + "" +
                "` by `" + tracks.get(0).author + "`");
        embed.setFooter("Total duration: ≈" + formatTime(totalTime));
        message.getChannel().sendMessageEmbeds(embed.build()).setMessageReference(message).queue();
    }

    private void loadPlaylist(CommandContext context, String identifier) {
        AudioPlaylist playlist = PlayerManager.getInstance().loadPlayList(identifier);

        if (playlist == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("No close matches were found! \nTry a more precise search query.");
            sendErrorEmbed(embed, context);
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());

        long totalTime = 0;

        AudioTrack selectedTrack = playlist.getSelectedTrack();

        if ((selectedTrack == null)){
            for (AudioTrack track : playlist.getTracks()) {
                musicManager.getScheduler().queue(track, context.getMember());
                totalTime = totalTime + track.getDuration();
                track.setUserData(context.getMember());
            }

        } else {
            ArrayList<AudioTrack> beforeSelectedTrack = new ArrayList<>();
            boolean reachedSelectedTrack = false;

            for (AudioTrack track : playlist.getTracks()){
                if (reachedSelectedTrack){
                    musicManager.getScheduler().queue(track, context.getMember());
                } else {
                    reachedSelectedTrack = track.getIdentifier().equals(selectedTrack.getIdentifier());
                    if (reachedSelectedTrack) {
                        musicManager.getScheduler().queue(track, context.getMember());
                    } else {
                        beforeSelectedTrack.add(track);
                    }
                }
                totalTime = totalTime + track.getDuration();
                track.setUserData(context.getMember());
            }
            for (AudioTrack track : beforeSelectedTrack){
                musicManager.getScheduler().queue(track, context.getMember());
            }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setAuthor("Queued playlist");
        embed.setTitle("Queued from: " + playlist.getName(), identifier);
        embed.setDescription("Queued " + playlist.getTracks().size() + " songs");
        if (selectedTrack == null) {
            selectedTrack = playlist.getTracks().get(0);
        }
        embed.setThumbnail("http://img.youtube.com/vi/" + selectedTrack.getInfo().identifier + "/0.jpg");
        embed.appendDescription("\nStarting from: `" + selectedTrack.getInfo().title + "" +
                "` by `" + selectedTrack.getInfo().author + "`");
        embed.setFooter("Total duration: " + formatTime(totalTime));
        context.respondEmbed(embed);
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
