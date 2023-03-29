package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.db.server.ServerData;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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

public class PauseSongCommand implements Command {
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

        if (!user.getVoiceState().inAudioChannel()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a voice channel!");
            embed.setFooter("You need to be in a voice channel in order to use this command!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (inSameVC(user, self)) {

            if (!hasPermissions(user) && !isVCEmpty(self)){
                EmbedBuilder embed = new EmbedBuilder();
                ServerData data = Bot.getINSTANCE().getGuildData(guild);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `"+ data.getDJRoleName() +"` (case sensitive) role or a role with the 'Manage Channels' permission to use");
                sendErrorEmbed(embed, event);
                return;
            }

            pauseSong(guild, event);
            return;

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The musicplayer is currently in a different VC!");
            embed.setFooter("You need to be in the same VC to use this command!");
            sendErrorEmbed(embed, event);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.ERROR);
        embed.setDescription("The music player is currently inactive!");
        sendErrorEmbed(embed, event);
    }

    private void pauseSong(Guild guild, Event event) {
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(guild).getAudioPlayer();
        audioPlayer.setPaused(!audioPlayer.isPaused());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (audioPlayer.isPaused()) {
            embed.setDescription("⏸ - The player is now paused");
        } else {
            embed.setDescription("▶ - The player is now playing");
        }

        sendEmbed(embed, event);
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
