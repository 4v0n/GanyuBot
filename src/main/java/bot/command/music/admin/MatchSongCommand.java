package bot.command.music.admin;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.music.DiscoveredVidId;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bot.command.music.MusicUtil.isURL;
import static bot.command.music.MusicUtil.sendErrorEmbed;

public class MatchSongCommand implements Command {

    @Override
    public void run(CommandContext context, List<String> args) {
        String spotifyId;
        String youtubeId;

        if (context.getEvent() instanceof MessageReceivedEvent) {
            if (args.size() < 2) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You have missed out on some/all of the required fields!");
                sendErrorEmbed(embed, context);
                return;
            }

            spotifyId = args.get(0);
            youtubeId = args.get(1);
        } else if (context.getEvent() instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
            spotifyId = event.getOption("spotifyid").getAsString();
            youtubeId = event.getOption("youtubeid").getAsString();

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("Oops, something went wrong!");
            embed.setFooter("Command triggered by non-message/slashcommand event");
            sendErrorEmbed(embed, context);
            return;
        }

        spotifyId = extractSpotifyIdIfNeeded(spotifyId);
        youtubeId = extractYoutubeIdIdNeeded(youtubeId);

        if (!isSpotifyId(spotifyId)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The spotify ID provided is invalid!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!isYoutubeId(youtubeId)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The youtube ID provided is invalid!");
            sendErrorEmbed(embed, context);
            return;
        }

        matchSong(context, spotifyId, youtubeId);
    }

    private String extractSpotifyIdIfNeeded(String spotifyId) {
        if (isURL(spotifyId)) {
            try {
                URL url = new URL(spotifyId);
                return url.getPath().substring(7);
            } catch (Exception ignored) {
            }
        }
        return spotifyId;
    }

    private String extractYoutubeIdIdNeeded(String youtubeId) {
        if (isURL(youtubeId)) {
            String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(youtubeId);
            if(matcher.find()){
                return matcher.group();
            }
        }
        return youtubeId;
    }

    private void matchSong(CommandContext context, String spotifyId, String youtubeId) {
        Track track = SpotifyManager.getInstance().getSongById(spotifyId);
        DiscoveredVidId song = new DiscoveredVidId(spotifyId, youtubeId, track.getName());
        Bot.getInstance().getDatastore().save(song);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Matched song");
        embed.setThumbnail("http://img.youtube.com/vi/" + youtubeId + "/0.jpg");
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription(track.getName() + " will now play this: " +
                "\nhttps://www.youtube.com/watch?v=" + youtubeId);
        context.respondEmbed(embed);
    }

    private boolean isSpotifyId(String spotifyId) {
        Track track = SpotifyManager.getInstance().getSongById(spotifyId);
        return track != null;
    }

    private boolean isYoutubeId(String youtubeId) {
        YoutubeAudioSourceManager yasm = new YoutubeAudioSourceManager();
        AudioItem track = yasm.loadTrackWithVideoId(youtubeId, false);
        return track != AudioReference.NO_TRACK;
    }

    @Override
    public @NotNull String getCommandWord() {
        return "match";
    }

    @Override
    public @NotNull String getDescription() {
        return "Matches a spotify songId to a youtube songId. " +
                "Usage: `[prefix] mp a match [SpotifyId/link] [YoutubeId/link]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "Matches a spotify songId to a youtube songId");

        OptionData spotifyField = new OptionData(
                OptionType.STRING,
                "spotifyid",
                "The song ID on spotify or link",
                true
        );

        OptionData youtubeField = new OptionData(
                OptionType.STRING,
                "youtubeid",
                "The song ID on youtube or link",
                true
        );

        commandData.addOptions(spotifyField, youtubeField);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
