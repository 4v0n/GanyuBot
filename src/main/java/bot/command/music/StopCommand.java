package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class StopCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        if (!inVC(context, true)) {
            return;
        }
        if (!playerActive(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        if (!hasPermissions(context, true)) {
            return;
        }
        stopMusicPlayer(context);
    }

    private void stopMusicPlayer(CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        context.getGuild().getAudioManager().closeAudioConnection();
        musicManager.getAudioPlayer().destroy();
        musicManager.getScheduler().getSongQueue().clear();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("\uD83D\uDC4B");

        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "stop";
    }

    @Override
    public @NotNull String getDescription() {
        return "stops the music player";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"leave", "disconnect"};
    }
}
