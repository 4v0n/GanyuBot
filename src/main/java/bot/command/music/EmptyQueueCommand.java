package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class EmptyQueueCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        if (!playerActive(context, true)) {
            return;
        }
        if (!inVC(context, true)) {
            return;
        }
        if (!inSameVC(context, true) || !hasPermissions(context, true)) {
            return;
        }
        emptySongQueue(context);
    }

    private void emptySongQueue(CommandContext context) {
        Guild guild = context.getGuild();
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        musicManager.getScheduler().getSongQueue().clear();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("✅");
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "emptyqueue";
    }

    @Override
    public @NotNull String getDescription() {
        return "Empties the song queue";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"empty", "eq", "clear"};
    }
}
