package bot.command.music;

import bot.command.CommandMethods;
import bot.command.directMessage.DMCommand;
import bot.feature.music.spotify.LoggedInSpotifyManager;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class authSpotifyDMCommand implements DMCommand {
    @Override
    public void run(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't provided a link!")
                    .setColor(ColorScheme.ERROR);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        String authCodeURL = args.get(0);
        if (!CommandMethods.isURL(authCodeURL)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You haven't provided a valid link!")
                    .setColor(ColorScheme.ERROR);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }

        URI uri;
        try {
            uri = new URI(authCodeURL);
            LoggedInSpotifyManager lism = SpotifyManager.getInstance().logInUser(event.getAuthor(), uri.getQuery().substring(5));
            User user = lism.getSpotifyApi().getCurrentUsersProfile().build().execute();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Linked spotify account")
                    .setThumbnail(user.getImages()[0].getUrl())
                    .setDescription("Connected to account: " + user.getDisplayName())
                    .setColor(ColorScheme.RESPONSE);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();

        } catch (URISyntaxException | IOException | SpotifyWebApiException | ParseException e) {
            //always ignored due to guard clause
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Something went wrong!")
                    .setDescription("Perhaps the link is invalid!")
                    .setColor(ColorScheme.ERROR);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "linkspotify";
    }

    @Override
    public @NotNull String getDescription() {
        return "links a spotify account";
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
