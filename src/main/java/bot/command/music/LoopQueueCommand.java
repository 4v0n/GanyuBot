package bot.command.music;

import bot.Bot;
import bot.command.Command;
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

public class LoopQueueCommand implements Command {
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

            loopQueue(guild, event);

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, event);
        }
    }

    private void loopQueue(Guild guild, Event event) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(guild).getScheduler();
        scheduler.toggleLoop();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isLoopQueue()) {
            embed.setDescription("\uD83D\uDD01 - Loop Queue is on");
        } else {
            embed.setDescription("Loop queue is off");
        }

        sendEmbed(embed, event);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "loopqueue";
    }

    @Override
    public @NotNull String getDescription() {
        return "loops the song queue";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"lq"};
    }
}
