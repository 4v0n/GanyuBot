package bot.command.music;

import bot.command.Command;
import bot.command.CommandContext;
import bot.util.ColorScheme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bot.command.music.MusicUtil.*;

public class JoinCommand implements Command {
    @Override
    public void run(CommandContext context, List<String> args) {
        if (inSameVC(context, false)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The bot is already in the same voice channel as you!");
            embed.setFooter("You don't need to call this command again!");
            sendErrorEmbed(embed, context);
            return;
        }
        if (!hasPermissions(context, true)) {
            return;
        }
        joinVoiceChannel(context);
    }

    private void joinVoiceChannel(CommandContext context) {
        Member user = context.getMember();
        Member self = context.getSelfMember();

        AudioChannel audioChannel = user.getVoiceState().getChannel();
        self.getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`" );
        embed.setColor(ColorScheme.RESPONSE);
        context.respondEmbed(embed);
    }

    @Override
    public @NotNull String getCommandWord() {
        return "join";
    }

    @Override
    public @NotNull String getDescription() {
        return "joins your current voice channel";
    }

    @Override
    public @NotNull CommandDataImpl getCommandData() {
        return new CommandDataImpl(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"j"};
    }


}
