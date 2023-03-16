package bot.command.music;

import bot.command.ICommand;
import bot.Bot;
import bot.util.ColorScheme;
import bot.db.server.ServerData;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.feature.music.lavaplayer.TrackScheduler;
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
import static bot.command.music.MusicMethods.sendErrorEmbed;

public class ShuffleQueueICommand implements ICommand {
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

            shuffleQueue(guild, event);

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a VC with the bot!");
            sendErrorEmbed(embed, event);
        }
    }

    private void shuffleQueue(Guild guild, Event event) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(guild).getScheduler();
        scheduler.toggleShuffle();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isShuffle()) {
            embed.setDescription("\uD83D\uDD00 - Shuffle play is on");
        } else {
            embed.setDescription("shuffle is off");
        }

        sendEmbed(embed, event);
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
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }
}
