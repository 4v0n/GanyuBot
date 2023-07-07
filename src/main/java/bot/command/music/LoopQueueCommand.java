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

public class LoopQueueCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        if (!inVC(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        if (!hasPermissions(context, true)) {
            return;
        }
        loopQueue(context);
    }

    private void loopQueue(CommandContext context) {
        Guild guild = context.getGuild();
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(guild).getScheduler();
        scheduler.toggleLoop();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isLoopQueue()) {
            embed.setDescription("\uD83D\uDD01 - Loop Queue is on");
        } else {
            embed.setDescription("Loop queue is off");
        }

        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "loopqueue";
    }

    @Override
    public @NotNull String getDescription() {
        return "loops the song queue";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"lq"};
    }
}
