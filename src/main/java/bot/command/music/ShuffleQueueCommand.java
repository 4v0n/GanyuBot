package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.lavaplayer.TrackScheduler;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class ShuffleQueueCommand implements Command {
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
        shuffleQueue(context);
    }

    private void shuffleQueue(CommandContext context) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(context.getGuild()).getScheduler();
        scheduler.toggleShuffle();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isShuffle()) {
            embed.setDescription("\uD83D\uDD00 - Shuffle play is on");
        } else {
            embed.setDescription("shuffle is off");
        }

        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "shuffle";
    }

    @Override
    public @NotNull String getDescription() {
        return "toggles shuffleplay";
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
