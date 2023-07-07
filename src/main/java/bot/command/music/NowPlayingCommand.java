package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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

public class NowPlayingCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        if (!playerActive(context, true)) {
            return;
        }
        showNowPlaying(context);
    }

    private void showNowPlaying(CommandContext context) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack track = musicManager.getAudioPlayer().getPlayingTrack();

        if (track == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is nothing playing at the moment!");
            embed.setFooter("Queue something up before using this command.");
            sendErrorEmbed(embed, context);

        } else {
            String string =
                    "`" + timeLine(track.getPosition(), track.getDuration()) + "`\n" +
                            "`" + formatTime(track.getPosition()) + " / " + formatTime(track.getDuration()) + "`";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Now playing");
            embed.setTitle(track.getInfo().author + " - " + track.getInfo().title, track.getInfo().uri);
            embed.setDescription(string);

            Member userData = (Member) track.getUserData();
            String id = userData.getEffectiveName();

            embed.setFooter(("Requested by: " + id), userData.getEffectiveAvatarUrl());
            embed.setColor(ColorScheme.RESPONSE);
            embed.setThumbnail("http://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg");

            context.respondEmbed(embed);
        }
    }

    @Override
    public @NotNull String getCommandWord() {
        return "nowplaying";
    }

    @Override
    public @NotNull String getDescription() {
        return "shows the currently playing song";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"np"};
    }
}
