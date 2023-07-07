package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class PauseSongCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        if (!inVC(context, true)) {
            return;
        }
        if (!(inSameVC(context, true) && hasPermissions(context, true))) {
            return;
        }
        pauseSong(context);
    }

    private void pauseSong(CommandContext context) {
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(context.getGuild()).getAudioPlayer();
        audioPlayer.setPaused(!audioPlayer.isPaused());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (audioPlayer.isPaused()) {
            embed.setDescription("⏸ - The player is now paused");
        } else {
            embed.setDescription("▶ - The player is now playing");
        }

        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "pause";
    }

    @Override
    public @NotNull String getDescription() {
        return "pauses the currently playing song";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
