package bot.command.music.admin;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.music.DiscoveredVidId;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import dev.morphia.Datastore;
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

import static bot.command.music.MusicUtil.isURL;
import static bot.command.music.MusicUtil.sendErrorEmbed;

public class ResetMatchCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        String spotifyId;
        if (context.getEvent() instanceof MessageReceivedEvent) {
            if (args.size() < 1) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You have missed out on some/all of the required fields!");
                sendErrorEmbed(embed, context);
                return;
            }

            spotifyId = args.get(0);
        } else if (context.getEvent() instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
            spotifyId = event.getOption("spotifyid").getAsString();

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("Oops, something went wrong!");
            embed.setFooter("Command triggered by non-message/slashcommand event");
            sendErrorEmbed(embed, context);
            return;
        }

        spotifyId = extractSpotifyIdIfNeeded(spotifyId);
        if (!isSpotifyId(spotifyId)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The spotify ID provided is invalid!");
            sendErrorEmbed(embed, context);
            return;
        }
        resetSong(context, spotifyId);
    }

    private void resetSong(CommandContext context, String spotifyId) {
        Datastore datastore = Bot.getINSTANCE().getDatastore();
        DiscoveredVidId item = DiscoveredVidId.getFromSpotifyId(spotifyId);
        if (item != null) {
            datastore.delete(item);
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Reset song");
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Song has been unlinked");
        context.respondEmbed(embed);
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

    private boolean isSpotifyId(String spotifyId) {
        Track track = SpotifyManager.getINSTANCE().getSongById(spotifyId);
        return track != null;
    }

    @Override
    public @NotNull String getCommandWord() {
        return "resetmatch";
    }

    @Override
    public @NotNull String getDescription() {
        return "Unbinds a spotify song from a youtube song. " +
                "Usage: `[prefix] mp a rm [SpotifyId/link]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "Unbinds a spotify song from a youtube song.");
        commandData.addOptions(
                new OptionData(
                        OptionType.STRING,
                        "spotifyid",
                        "The song ID on spotify or song link",
                        true
                )
        );
        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"rm","reset"};
    }
}
