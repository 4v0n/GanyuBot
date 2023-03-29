package bot.command.music;

import bot.command.Command;
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
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;

        if (event instanceof MessageReceivedEvent) {
            user = ((MessageReceivedEvent) event).getMember();
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
        }

        if (event instanceof SlashCommandInteractionEvent) {
            user = ((SlashCommandInteractionEvent) event).getMember();
            self = ((SlashCommandInteractionEvent) event).getGuild().getSelfMember();
        }

        if (!user.getVoiceState().inAudioChannel()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a voice channel!");
            embed.setFooter("Join a voice channel before using this command!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (inSameVC(user, self)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("The bot is already in the same voice channel as you!");
            embed.setFooter("You don't need to call this command again!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (isVCEmpty(self)) {
            joinVoiceChannel(user, self, event);
            return;

        } else {

            if (!hasPermissions(user)){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("The bot is already in another VC!");
                embed.setFooter("Join VC: `" + self.getVoiceState().getChannel().getName() + "` or wait for the users to finish");
                sendErrorEmbed(embed, event);

            } else {
                joinVoiceChannel(user, self, event);
            }
        }
    }

    private void joinVoiceChannel(Member user, Member self, Event event) {
        AudioChannel audioChannel = user.getVoiceState().getChannel();
        self.getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`" );
        embed.setColor(ColorScheme.RESPONSE);
        sendEmbed(embed, event);
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
