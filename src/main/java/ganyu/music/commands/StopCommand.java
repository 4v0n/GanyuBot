package ganyu.music.commands;

import ganyu.base.Bot;
import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import ganyu.data.ServerData;
import ganyu.music.MusicManager;
import ganyu.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ganyu.music.commands.MusicMethods.*;

public class StopCommand implements Command {
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

        if (inSameVC(user, self)){
            if (!hasPermissions(user) && !isVCEmpty(user)){
                ServerData data = Bot.getINSTANCE().getGuildData().get(guild);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(ColorScheme.ERROR);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `"+ data.getDJRoleName() +"` (case sensitive) role or a role with the `Manage Channels` permission to use.");
                sendErrorEmbed(embed, event);
                return;
            }

            stopMusicPlayer(guild, event);

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, event);
        }
    }

    private void stopMusicPlayer(Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        guild.getAudioManager().closeAudioConnection();
        musicManager.getAudioPlayer().destroy();
        musicManager.getScheduler().getSongQueue().clear();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("\uD83D\uDC4B");

        sendEmbed(embed, event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "stop";
    }

    @Override
    public @NotNull String getDescription() {
        return "stops the music player";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"leave", "disconnect"};
    }
}
