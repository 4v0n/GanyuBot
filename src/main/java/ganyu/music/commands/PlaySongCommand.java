package ganyu.music.commands;

import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import ganyu.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ganyu.music.commands.MusicMethods.*;

public class PlaySongCommand implements Command {
    @Override
    public void run(Event event, List<String> args) {
        Member user = null;
        Member self = null;
        String link = null;

        if (event instanceof MessageReceivedEvent) {
            user = ((MessageReceivedEvent) event).getMember();
            self = ((MessageReceivedEvent) event).getGuild().getSelfMember();
            link = String.join(" ", args);
        }

        if (event instanceof SlashCommandEvent) {
            user = ((SlashCommandEvent) event).getMember();
            self = ((SlashCommandEvent) event).getGuild().getSelfMember();

            link = ((SlashCommandEvent) event).getOption("query").getAsString();
        }

        if (!user.getVoiceState().inAudioChannel()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You are not in a voice channel!");
            embed.setFooter("Join a voice channel before using this command!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (link == null || link.isBlank() || link.isEmpty()){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("You haven't provided a link / search query for a song!");
            sendErrorEmbed(embed, event);
            return;
        }

        if (inSameVC(user, self)) {
            queueSong(event,link, user);
            return;
        }

        if (isVCEmpty(self)) {
            joinVoiceChannel(user, self, event);
            queueSong(event,link, user);

        } else {

            if (!hasPermissions(user)){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("The bot is already in another VC!");
                embed.setFooter("Join VC: `" + self.getVoiceState().getChannel().getName() + "` or wait for the users to finish");
                sendErrorEmbed(embed, event);

            } else {
                joinVoiceChannel(user, self, event);
                queueSong(event,link, user);
            }
        }
    }

    private void queueSong(Event event, String link, Member user) {
        if (!isURL(link)){
            link = link = "ytsearch:" + link + "audio";
        }

        PlayerManager.getInstance().loadAndPlay(event, link, user);
    }

    private void joinVoiceChannel(Member user, Member self, Event event) {
        AudioChannel audioChannel = user.getVoiceState().getChannel();
        self.getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`" );
        embed.setColor(ColorScheme.RESPONSE);

        if (event instanceof MessageReceivedEvent){
            sendEmbed(embed, event);
        }
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
    public @NotNull CommandData getCommandData() {
        CommandData commandData = new CommandData(getCommandWord(), "queues a song and then plays a song");
        commandData.addOption(OptionType.STRING, "query", "The link / search query for a certain song.", true);

        return commandData;
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"p"};
    }
}
