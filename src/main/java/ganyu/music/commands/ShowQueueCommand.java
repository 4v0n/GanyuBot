package ganyu.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import ganyu.base.ColorScheme;
import ganyu.command.message.Command;
import ganyu.command.templatemessage.MultiPageEmbed;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static ganyu.music.commands.MusicMethods.*;

public class ShowQueueCommand implements Command {

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

        if (self.getVoiceState().inAudioChannel()){
            showSongQueue(guild, event);
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("The music player is currently inactive!");
            embed.setColor(ColorScheme.ERROR);
            sendErrorEmbed(embed, event);
        }
    }

    private void showSongQueue(Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        final BlockingQueue<AudioTrack> queue = musicManager.getScheduler().getSongQueue();

        if (queue.isEmpty() && (musicManager.getAudioPlayer().getPlayingTrack() == null || musicManager.getAudioPlayer().isPaused())) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is nothing playing at the moment!");
            sendErrorEmbed(embed, event);
            return;
        }


        AudioTrack currentTrack = musicManager.getAudioPlayer().getPlayingTrack();
        AudioTrackInfo trackInfo = currentTrack.getInfo();

        String name = "<@" + ((Member) currentTrack.getUserData()).getId() + ">";

        String nowPlayingString = "`" + trackInfo.title + "` by `" + trackInfo.author + "`" +
                " - `" + formatTime(currentTrack.getPosition()) + "/" + formatTime(currentTrack.getDuration()) + "` - " +
                name + "\n";

        if (queue.isEmpty()) {
            showNowPlaying(guild, event);
        }

        ArrayList<AudioTrack> trackList = new ArrayList<>(queue);
        ArrayList<String> trackStrings = new ArrayList<>();

        int i = 0;
        long totalTime = 0;
        for (AudioTrack track : trackList) {
            i++;
            AudioTrackInfo info = track.getInfo();

            String id = "<@" + ((Member) track.getUserData()).getId() + ">";

            String string = i + " - `" + info.title + "` by `" + info.author + "` - " +
                    " `" + formatTime(track.getDuration()) + "` - " +
                    id;
            trackStrings.add(string);

            totalTime = totalTime + track.getDuration();
        }

        MultiPageEmbed queueListMessage = new MultiPageEmbed(trackStrings, 10);
        queueListMessage.setColor(ColorScheme.RESPONSE);
        queueListMessage.setAuthor("Song queue");
        queueListMessage.setTitle("Now playing:", trackInfo.uri);
        queueListMessage.setDescription(nowPlayingString);
        queueListMessage.setThumbnail("http://img.youtube.com/vi/" + trackInfo.identifier + "/0.jpg");

        if (event instanceof MessageReceivedEvent) {
            queueListMessage.sendMessage(((MessageReceivedEvent) event).getChannel());
            return;
        }

        if (event instanceof SlashCommandEvent){
            queueListMessage.replyTo((SlashCommandEvent) event);
        }
    }

    private void showNowPlaying(Guild guild, Event event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioTrack track = musicManager.getAudioPlayer().getPlayingTrack();

        if (track == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(ColorScheme.ERROR);
            embed.setDescription("There is nothing playing at the moment!");
            embed.setFooter("Queue something up before using this command.");
            sendErrorEmbed(embed, event);

        } else {
            String string =
                    "`" + timeLine(track.getPosition(), track.getDuration()) + "`\n" +
                            "`" + formatTime(track.getPosition()) + " / " + formatTime(track.getDuration()) + "`";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Now playing");
            embed.setTitle(track.getInfo().author + " - " + track.getInfo().title, track.getInfo().uri);
            embed.setDescription(string);

            Member userData = (Member) track.getUserData();
            String id = userData.getEffectiveName();

            embed.setFooter(("Requested by: " + id), userData.getEffectiveAvatarUrl());
            embed.setColor(ColorScheme.RESPONSE);
            embed.setThumbnail("http://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg");

            sendEmbed(embed, event);
        }
    }


    @Override
    public @NotNull String getCommandWord() {
        return "queue";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns the list of songs in the queue";
    }

    @Override
    public @NotNull CommandData getCommandData() {
        return new CommandData(getCommandWord(), getDescription());
    }

    @Override
    public String[] getSynonyms() {
        return new String[]{"q"};
    }
}