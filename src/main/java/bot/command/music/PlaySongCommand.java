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

public class PlaySongCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        String link = null;
        Event event = context.getEvent();

        if (event instanceof MessageReceivedEvent) {
            link = String.join(" ", args);
        }

        if (event instanceof SlashCommandInteractionEvent) {
            link = ((SlashCommandInteractionEvent) event).getOption("query").getAsString();
        }

        if (link == null || link.isBlank() || link.isEmpty()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a link / search query for a song!");
            sendErrorEmbed(embed, context);
            return;
        }

        if (!playerActive(context, false)) {
            if (isVCEmpty(context, true)) {
                joinVoiceChannel(context);
                queueSong(context, link);
            }
            return;
        }
        if (!inVC(context, true)) {
            return;
        }
        if (!inSameVC(context, true)) {
            return;
        }
        queueSong(context, link);
    }

    private void queueSong(CommandContext context, String link) {
        if (!isURL(link)){
            link = link = "ytsearch:" + link + "audio";
        }
        PlayerManager.getInstance().loadAndPlay(context, link, context.getMember());
    }

    private void joinVoiceChannel(CommandContext context) {
        AudioChannel audioChannel = context.getMember().getVoiceState().getChannel();
        context.getSelfMember().getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`" );
        embed.setColor(ColorScheme.RESPONSE);

        context.getMessageChannel().sendMessageEmbeds(embed.build()).queue();
    }



    @Override
    public @NotNull String getCommandWord() {
        return "play";
    }

    @Override
    public @NotNull String getDescription() {
        return "queues a song and then plays a song." +
                " Usage: `[prefix] mp play [link / search query]`";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl(getCommandWord(), "queues a song and then plays a song");
        commandData.addOption(OptionType.STRING, "query", "The link / search query for a certain song.", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"p"};
    }
}
