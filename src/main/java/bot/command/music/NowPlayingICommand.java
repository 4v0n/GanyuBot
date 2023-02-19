package bot.command.music;

import bot.command.ICommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import bot.util.ColorScheme;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicMethods.*;

public class NowPlayingICommand implements ICommand {
    @Override
    public void run(Event event, List<String> args) {
        Member self = null;
        Guild guild = null;

        if (event instanceof MessageReceivedEvent) {
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            guild = ((MessageReceivedEvent) event).getGuild();
        }

        if (event instanceof SlashCommandEvent) {
            self = ((SlashCommandEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandEvent) event).getGuild();
        }

        if (self.getVoiceState().inAudioChannel()){
            showNowPlaying(guild, event);
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("The music player is currently inactive!");
            embed.setColor(ColorScheme.ERROR);
            sendErrorEmbed(embed, event);
        }
    }

    private void showNowPlaying(Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioTrack track = musicManager.getAudioPlayer().getPlayingTrack();

        if (track == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is nothing playing at the moment!");
            embed.setFooter("Queue something up before using this command.");
            sendErrorEmbed(embed, event);

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

            sendEmbed(embed, event);
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
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"np"};
    }
}
