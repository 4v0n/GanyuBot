package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.spotify.SpotifyManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

public class UnlinkSpotifyCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        User user = context.getAuthor();
        SpotifyManager instance = SpotifyManager.getInstance(user);

        try {
            se.michaelthelin.spotify.model_objects.specification.User spotifyUser = instance.getSpotifyApi().getCurrentUsersProfile().build().execute();
            if (spotifyUser != null) {
                SpotifyManager.removeUser(user);
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Account not connected")
                .setColor(ColorScheme.ERROR)
                .setDescription("Your account is not linked");
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "unlinkspotify";
    }

    @Override
    public @NotNull String getDescription() {
        return "Unlinks your spotify account from the bot";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"unlink", "uls"};
    }
}
