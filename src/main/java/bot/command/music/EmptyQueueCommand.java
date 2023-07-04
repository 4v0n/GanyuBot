package bot.command.music;

import bot.Bot;
import bot.command.Command;
import bot.command.CommandContext;
import bot.db.legacy.server.ServerData;
import bot.feature.music.MusicManager;
import bot.feature.music.lavaplayer.PlayerManager;
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

public class EmptyQueueCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Member user = context.getMember();
        Member self = context.getSelfMember();
        Guild guild = context.getGuild();

        if (!user.getVoiceState().inAudioChannel()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a voice channel!");
            embed.setFooter("You need to be in a voice channel in order to use this command!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (inSameVC(user, self)) {

            if (!hasPermissions(user) && !isVCEmpty(self)){
                EmbedBuilder embed = new EmbedBuilder();
                ServerData data = Bot.getINSTANCE().getGuildData(guild);
                embed.setDescription("You don't have the permissions to use this command!");
                embed.setFooter("This command requires the `"+ data.getDJRoleName() +"` (case sensitive) role or a role with the 'Manage Channels' permission to use");
                sendErrorEmbed(embed, context);
                return;
            }

            emptySongQueue(guild, context);
            return;

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The musicplayer is currently in a different VC!");
            embed.setFooter("You need to be in the same VC to use this command!");
            sendErrorEmbed(embed, context);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.ERROR);
        embed.setDescription("The music player is currently inactive!");
        sendErrorEmbed(embed, context);
    }

    private void emptySongQueue(Guild guild, CommandContext context) {
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
