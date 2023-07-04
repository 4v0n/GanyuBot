package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.feature.music.lavaplayer.PlayerManager;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class PlayListCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        Event event = context.getEvent();
        Member user = context.getMember();
        Member self = context.getSelfMember();
        String link = null;

        if (event instanceof MessageReceivedEvent) {
            link = String.join(" ", args);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            link = ((SlashCommandInteractionEvent) event).getOption("url").getAsString();
        }

        if (!user.getVoiceState().inAudioChannel()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a voice channel!");
            embed.setFooter("Join a voice channel before using this command!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (link == null || link.isBlank() || link.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a link / search query for a song!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!isURL(link)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("That is not a link!\nYou need to provide a url for this command to work!");
            embed.setColor(ColorScheme.ERROR);
            sendErrorEmbed(embed, context);
        }

        if (inSameVC(user, self)) {
            queuePlaylist(context, link, user);
            return;
        }

        if (isVCEmpty(self)) {
            joinVoiceChannel(user, self, context);
            queuePlaylist(context, link, user);

        } else {

            if (!hasPermissions(user)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("The bot is already in another VC!");
                embed.setFooter("Join VC: `" + self.getVoiceState().getChannel().getName() + "` or wait for the users to finish");
                sendErrorEmbed(embed, context);

            } else {
                joinVoiceChannel(user, self, context);
                queuePlaylist(context, link, user);
            }
        }
    }

    private void joinVoiceChannel(Member user, Member self, CommandContext context) {
        AudioChannel audioChannel = user.getVoiceState().getChannel();
        self.getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`");
        embed.setColor(ColorScheme.RESPONSE);

        context.respondEmbed(embed);
    }

    private void queuePlaylist(CommandContext context, String link, Member user) {
        PlayerManager.getInstance().loadPlaylist(context, link, user);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "playlist";
    }

    @Override
    public @NotNull String getDescription() {
        return "Adds a playlist of songs to the queue" +
                " Usage: `[prefix] mp playlist [link]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), getDescription());
        commandData.addOption(OptionType.STRING, "url", "The URL to the playlist", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"pl"};
    }
}
