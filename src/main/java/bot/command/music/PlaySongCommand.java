package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.db.music.DiscoveredVidId;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.lavaplayer.TrackScheduler;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static bot.command.music.MusicUtil.*;

public class PlaySongCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        String link = null;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            link = String.join(" ", args);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            link = ((SlashCommandInteractionEvent) event).getOption("query").getAsString();
        }

        if (link == null || link.isBlank() || link.isEmpty()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a link / search query for a song!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!playerActive(context, false)) {
            if (isVCEmpty(context, true)) {
                joinVoiceChannel(context);
                queueSong(context, link);
            }
            return;
        }
        if (!inVC(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        queueSong(context, link);
    }

    private void queueSong(CommandContext context, String link) {
        if (!isURL(link)){
            link = "ytsearch:" + link + " audio";
        } else {
            URI uri = null;
            try {
                uri = new URI(link);
            } catch (URISyntaxException ignored) {
                // will always be ignored due to guard clause
            }

            if (uri.getAuthority().equals("open.spotify.com")) {
                queueSpotifySong(context, link);
                return;
            }
        }

        loadSong(context, link);
    }

    private void queueSpotifySong(CommandContext context, String link) {
        EmbedBuilder warning = new EmbedBuilder();
        warning.setAuthor("Queuing song");
        warning.setDescription("Songs found from spotify links may not be accurate or may not even be found!");
        warning.setColor(ColorScheme.INFO);
        context.getMessageChannel().sendMessageEmbeds(warning.build()).queue();

        Track track = SpotifyManager.getInstance().getSong(link);
        String ytId = DiscoveredVidId.getYoutubeIdFromSpotifyId(track.getId());
        String query;

        if (ytId != null) {
            query = "https://youtu.be/" + ytId;
        } else {
            query = ("ytsearch:" + buildArtistString(track.getArtists()) + "- " + track.getName());
        }

        loadSong(context, query);
    }

    private void loadSong(CommandContext context, String identifier) {
        AudioTrack audioTrack = PlayerManager.getInstance().loadTrack(identifier);
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(context.getGuild()).getScheduler();
        audioTrack.setUserData(context.getMember());
        scheduler.queue(audioTrack);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        embed.setAuthor("Added to queue");
        embed.setTitle(audioTrack.getInfo().author + " - " + audioTrack.getInfo().title, audioTrack.getInfo().uri);
        embed.setThumbnail("http://img.youtube.com/vi/" + audioTrack.getInfo().identifier + "/0.jpg");

        int position = scheduler.getPositionOfTrack(audioTrack);
        if (position > 0) {
            embed.setDescription("Position in queue: " + (position));
            embed.setFooter("Duration: " + formatTime(audioTrack.getDuration()) + " | Time until song: " + formatTime(scheduler.getTimeUntilTrack(audioTrack)));
        } else {
            embed.setFooter("Duration: " + formatTime(audioTrack.getDuration()));
        }

        context.respondEmbed(embed);
    }

    private String buildArtistString(ArtistSimplified[] artists) {
        StringBuilder sb = new StringBuilder();
        for (ArtistSimplified artist : artists) {
            sb.append(artist.getName());
            sb.append(" ");
        }
        return sb.toString();
    }

    private void joinVoiceChannel(CommandContext context) {
        AudioChannel audioChannel = context.getMember().getVoiceState().getChannel();
        context.getSelfMember().getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`" );
        embed.setColor(ColorScheme.RESPONSE);

        context.getMessageChannel().sendMessageEmbeds(embed.build()).queue();
    }



    @Override
    public @NotNull String getCommandWord() {
        return "play";
    }

    @Override
    public @NotNull String getDescription() {
        return "queues a song and then plays a song." +
                " Usage: `[prefix] mp play [link / search query]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "queues a song and then plays a song");
        commandData.addOption(OptionType.STRING, "query", "The link / search query for a certain song.", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"p"};
    }
}
