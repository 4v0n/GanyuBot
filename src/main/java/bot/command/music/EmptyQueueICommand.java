package bot.command.music;

import bot.command.ICommand;
import bot.Bot;
import bot.util.ColorScheme;
import bot.db.server.ServerData;
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

public class EmptyQueueICommand implements ICommand {
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

        if (event instanceof SlashCommandEvent) {
            user = ((SlashCommandEvent) event).getMember();
            self = ((SlashCommandEvent) event).getGuild().getSelfMember();
            guild = ((SlashCommandEvent) event).getGuild();
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
                ServerData data = Bot.getINSTANCE().getGuildData().get(guild);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `"+ data.getDJRoleName() +"` (case sensitive) role or a role with the 'Manage Channels' permission to use");
                sendErrorEmbed(embed, event);
                return;
            }

            emptySongQueue(guild, event);
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

    private void emptySongQueue(Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        musicManager.getScheduler().getSongQueue().clear();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("✅");
        sendEmbed(embed, event);
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
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"empty", "eq", "clear"};
    }
}
