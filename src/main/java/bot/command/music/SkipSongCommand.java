package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.db.server.ServerData;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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

public class SkipSongCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;
        Guild guild = null;

        if (event instanceof MessageReceivedEvent) {
            user = ((MessageReceivedEvent) event).getMember();
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            guild = ((MessageReceivedEvent) event).getGuild();

        }

        if (event instanceof SlashCommandInteractionEvent) {
            user = ((SlashCommandInteractionEvent) event).getMember();
            self = ((SlashCommandInteractionEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandInteractionEvent) event).getGuild();
        }

        if (inSameVC(user, self)){
            if (!hasPermissions(user) && !isVCEmpty(user)){
                ServerData data = Bot.getINSTANCE().getGuildData(guild);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `"+ data.getDJRoleName() +"` (case sensitive) role or a role with the `Manage Channels` permission to use.");
                sendErrorEmbed(embed, event);
                return;
            }

            skipSong(guild, event);

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, event);
        }
    }

    private void skipSong(Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioTrackInfo currentTrack = musicManager.getAudioPlayer().getPlayingTrack().getInfo();

        if (currentTrack == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is currently no song playing!");
            sendErrorEmbed(embed, event);
            return;
        }

        musicManager.getAudioPlayer().stopTrack();

        if (!musicManager.getScheduler().getSongQueue().isEmpty()) {
            musicManager.getScheduler().nextTrack();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Skipped `" + currentTrack.title + "` by `" + currentTrack.author + "`");
        embed.setColor(ColorScheme.RESPONSE);
        sendEmbed(embed, event);
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
        return new String[0];
    }
}
