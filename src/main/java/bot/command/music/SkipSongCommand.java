package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class SkipSongCommand implements Command {
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
        skipSong(context);
    }

    private void skipSong(CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrackInfo currentTrack = musicManager.getAudioPlayer().getPlayingTrack().getInfo();

        if (currentTrack == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is currently no song playing!");
            sendErrorEmbed(embed, context);
            return;
        }

        musicManager.getAudioPlayer().stopTrack();

        if (!musicManager.getScheduler().getSongQueue().isEmpty()) {
            musicManager.getScheduler().nextTrack();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Skipped `" + currentTrack.title + "` by `" + currentTrack.author + "`");
        embed.setColor(ColorScheme.RESPONSE);
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "skip";
    }

    @Override
    public @NotNull String getDescription() {
        return "skips the currently playing song";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"s"};
    }
}
