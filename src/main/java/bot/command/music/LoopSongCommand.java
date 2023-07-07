package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.lavaplayer.TrackScheduler;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class LoopSongCommand implements Command {
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
        loopSong(context);
    }

    private void loopSong(CommandContext context) {
        Guild guild = context.getGuild();
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(guild).getScheduler();
        scheduler.toggleLoopSong();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isLoopSong()) {
            embed.setDescription("\uD83D\uDD02 - Loop is on");
        } else {
            embed.setDescription("Loop is off");
        }

        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "loop";
    }

    @Override
    public @NotNull String getDescription() {
        return "loops the currently playing song";
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
