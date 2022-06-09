package ganyu.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import ganyu.base.ColorScheme;
import ganyu.command.message.CommandHandler;
import ganyu.command.templatemessage.MultiPageMessage;
import ganyu.music.lavaplayer.PlayerManager;
import ganyu.music.lavaplayer.TrackScheduler;
import ganyu.music.vote.VoteMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This handles music bot commands
 *
 * @author Aron Navodh Kumarawatta
 * @version 09.06.2022
 */
public class MusicParser extends CommandHandler {

    /**
     * Creates a MusicParser object
     */
    public MusicParser() {
        super(2);
    }

    /**
     * Outlines short names for commands for easier access
     */
    @Override
    public void buildSynonyms() {
        addSynonym("j", "join");
        addSynonym("p", "play");
        addSynonym("pl", "playlist");
        addSynonym("eq", "emptyqueue");
        addSynonym("q", "queue");
        addSynonym("np", "nowplaying");
        addSynonym("empty", "emptyqueue");
        addSynonym("clear", "emptyqueue");
        addSynonym("leave", "stop");
        addSynonym("lq", "loopqueue");
        addSynonym("rm", "remove");
        addSynonym("disconnect", "stop");
        addSynonym("rd", "removeduplicates");
    }

    /**
     * Builds all commands.
     */
    @Override
    public void buildCommands() {
        // voice channel commands
        addCommand("join", "joins your current voice channel", this::joinVCCommand);
        addCommand("stop", "stops the music player", this::stopCommand);

        // song commands
        addCommand("play", "queues a song and then plays a song." +
                " Usage: `[prefix] play [link / search query]`", this::playSong);
        addCommand("playlist", "Adds a playlist of songs to the queue" +
                " Usage: `[prefix] playlist [link]`", this::queueListCommand);

        // queue information commands
        addCommand("nowplaying", "shows the currently playing song", this::nowPlayingCommand);
        addCommand("queue", "Returns the list of songs in the queue", this::showQueue);

        // queue manipulation commands
        addCommand("emptyqueue", "Empties the song queue", this::emptyQueue);
        addCommand("remove", "removes a song from the queue by number", this::removeSongCommand);
        addCommand("skipto", "skips all songs until a chosen point. Usage: `[prefix] skipto [number]`", this::skipToCommand);
        addCommand("move", "moves a track from one position to another. Usage: `[prefix] move [oldpos] [newpos]`", this::moveCommand);
        addCommand("removeduplicates", "removes all duplicate songs from the song queue", this::removeDuplicatesCommand);

        // player commands
        addCommand("pause", "pauses the currently playing song", this::pauseSongCommand);
        addCommand("skip", "skips the currently playing song", this::skipCommand);
        addCommand("shuffle", "toggles shuffleplay", this::shuffleCommand);
        addCommand("loopqueue", "loops the song queue", this::loopQueueCommand);
        addCommand("loop", "loops the currently playing song", this::loopSongCommand);
        addCommand("seek", "Allows you to seek backwards or forwards by a certain amount (negative to seek back). " +
                "Usage: `[prefix] seek (-)[time in seconds]`", this::seekCommand);

        addHelpMessage("Note that these commands can be directly accessed using `[prefix]m [command]`");
    }

    private void joinVCCommand(MessageReceivedEvent event, List<String> list) {
        if (!userInVC(event)) {
            // user not in a VC
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a Voice channel!",
                            "Join a voice channel before using this command!"
                    ).build()
            ).queue();
            return;
        }

        if (inSameVC(event)) {

            // user in same VC as bot
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "The bot is already in the same voice channel!",
                            "You don't need to use the join command again!"
                    ).build()
            ).queue();
            return;

        } else {
            // user in different VC from bot

            if (isVCEmpty(event)) {
                // bot vc is empty
                joinVoiceChannel(event);

            } else {
                if (!hasPermissions(event.getMember())) {
                    //bot in different VC
                    event.getChannel().sendMessageEmbeds(errorEmbed(
                                    "The bot is already in another VC!",
                                    "Join VC: `" +
                                            Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getSelfMember().getVoiceState()).getChannel()).getName() +
                                            "` or wait for the users to finish"
                            ).build()
                    ).queue();
                    return;
                }
            }
        }
        // user in VC and bot not in VC
    }

    private void joinVoiceChannel(MessageReceivedEvent event) {
        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        Member self = event.getGuild().getSelfMember();

        self.getGuild().getAudioManager().openAudioConnection(audioChannel);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Joining channel: `" + audioChannel.getName() + "`");
        embed.setColor(ColorScheme.RESPONSE);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void stopCommand(MessageReceivedEvent event, List<String> list) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            stopMusicPlayer(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void stopMusicPlayer(MessageReceivedEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.getAudioPlayer().stopTrack();
        musicManager.getScheduler().getSongQueue().clear();

        event.getMessage().addReaction("\uD83D\uDC4B").queue();
    }

    private void playSong(MessageReceivedEvent event, List<String> args) {
        if (!userInVC(event)) {
            // user not in a VC
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a Voice channel!",
                            "Join a voice channel before using this command!"
                    ).build()
            ).queue();
            return;
        }

        if (inSameVC(event)) {
            // user in same VC as bot
            queueSong(event, args);

        } else {
            // user in different VC from bot

            if (isVCEmpty(event)) {
                // bot vc is empty
                joinVoiceChannel(event);
                queueSong(event, args);

            } else {
                if (!hasPermissions(event.getMember())) {
                    //bot in different VC
                    event.getChannel().sendMessageEmbeds(errorEmbed(
                                    "The bot is already in another VC!",
                                    "Join VC: `" +
                                            Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getSelfMember().getVoiceState()).getChannel()).getName() +
                                            "` or wait for the users to finish"
                            ).build()
                    ).queue();
                    return;
                }
            }
        }
        // user in VC and bot not in VC
    }

    private void queueSong(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You need to supply the name of a song or a link!");
            embed.setColor(ColorScheme.ERROR);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        String link = String.join(" ", args);

        if (!isURL(link)) {
            link = "ytsearch:" + link + "audio";
        }

        PlayerManager.getInstance().loadAndPlay(event, link, event.getMember());
    }

    private void queueListCommand(MessageReceivedEvent event, List<String> args) {
        if (!userInVC(event)) {
            // user not in a VC
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a Voice channel!",
                            "Join a voice channel before using this command!"
                    ).build()
            ).queue();
            return;
        }

        if (inSameVC(event)) {
            // user in same VC as bot
            queueSongList(event, args);

        } else {
            // user in different VC from bot

            if (isVCEmpty(event)) {
                // bot vc is empty
                joinVoiceChannel(event);
                queueSongList(event, args);

            } else {
                if (!hasPermissions(event.getMember())) {
                    //bot in different VC
                    event.getChannel().sendMessageEmbeds(errorEmbed(
                                    "The bot is already in another VC!",
                                    "Join VC: `" +
                                            Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getSelfMember().getVoiceState()).getChannel()).getName() +
                                            "` or wait for the users to finish"
                            ).build()
                    ).queue();
                    return;
                }
            }
        }
        // user in VC and bot not in VC
    }

    private void queueSongList(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You need to provide a URL to the playlist!");
            embed.setColor(ColorScheme.ERROR);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        String link = String.join(" ", args);

        if (!isURL(link)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("You need to provide a URL to the playlist!");
            embed.setColor(ColorScheme.ERROR);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        PlayerManager.getInstance().loadPlaylist(event, link, event.getMember());
    }

    private void nowPlayingCommand(MessageReceivedEvent event, List<String> args) {
        if (isInVC(event)) {
            showNowPlaying(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "The music player is currently inactive!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void showNowPlaying(MessageReceivedEvent event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrack track = musicManager.getAudioPlayer().getPlayingTrack();

        if (track == null) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                    "There is nothing playing at the moment!",
                    "Queue something up before using this command."
            ).build()).queue();
            return;

        } else {
            String string = "Link: " + track.getInfo().uri + " \n" +
                    "`" + track.getInfo().title + "` by `" + track.getInfo().author + "` \n" +
                    "`" + timeLine(track.getPosition(), track.getDuration()) + "`\n" +
                    "`" + formatTime(track.getPosition()) + " / " + formatTime(track.getDuration()) + "`";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Now playing:");
            embed.setDescription(string);

            Member userData = (Member) track.getUserData();
            String id = userData.getEffectiveName();

            embed.setFooter(("Requested by: " + id), userData.getEffectiveAvatarUrl());
            embed.setColor(ColorScheme.RESPONSE);
            embed.setThumbnail("http://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg");

            event.getChannel().sendMessageEmbeds(embed.build()).queue();

        }
    }

    private void showQueue(MessageReceivedEvent event, List<String> args) {
        if (isInVC(event)) {
            showSongQueue(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "The music player is currently inactive!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void showSongQueue(MessageReceivedEvent event) {
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.getScheduler().getSongQueue();

        if (queue.isEmpty() && (musicManager.getAudioPlayer().getPlayingTrack() == null || musicManager.getAudioPlayer().isPaused())) {
            event.getChannel().sendMessage("The queue is empty!").queue();
            return;
        }


        AudioTrack currentTrack = musicManager.getAudioPlayer().getPlayingTrack();
        AudioTrackInfo trackInfo = currentTrack.getInfo();

        String name = "<@" + ((Member) currentTrack.getUserData()).getId() + ">";

        String nowPlayingString = "***Now playing: `" + trackInfo.title + "` by `" + trackInfo.author + "`***" +
                " - `" + formatTime(currentTrack.getPosition()) + "/" + formatTime(currentTrack.getDuration()) + "` requested by " +
                name + "\n \n";

        if (queue.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Song queue: ");
            embed.setDescription(nowPlayingString);
            embed.setColor(ColorScheme.RESPONSE);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
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
                    " `" + formatTime(track.getDuration()) + "` requested by " +
                    id;
            trackStrings.add(string);

            totalTime = totalTime + track.getDuration();
        }


        MultiPageMessage queueListMessage = new MultiPageMessage(
                "Song queue:",
                (nowPlayingString +
                        "Total Queue time: `" + formatTime(totalTime) + "`\n"),
                trackStrings,
                ColorScheme.RESPONSE,
                10
        );

        queueListMessage.sendMessage(event.getChannel());
    }

    private void emptyQueue(MessageReceivedEvent event, List<String> args) {
        if (!userInVC(event)) {
            // user not in a VC
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a Voice channel!",
                            "Join a voice channel before using this command!"
                    ).build()
            ).queue();
            return;
        }

        if (inSameVC(event)) {
            // user in same VC as bot
            emptySongQueue(event, args);

            return;
        } else {
            // user in different VC from bot
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "The music player is currently in a different VC!",
                            "You must be in the same VC to control the music player"
                    ).build()
            ).queue();

        }
        // user in VC and bot not in VC
        event.getChannel().sendMessageEmbeds(errorEmbed(
                        "The music player is currently inactive!",
                        ""
                ).build()
        ).queue();
    }

    private void emptySongQueue(MessageReceivedEvent event, List<String> args) {
        Member member = event.getMember();

        if (!hasPermissions(Objects.requireNonNull(member))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You don't have the permissions to use this command!",
                            "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                    ).build()
            ).queue();
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.getScheduler().getSongQueue().clear();

        event.getMessage().addReaction("✅").queue();
    }

    private void removeSongCommand(MessageReceivedEvent event, List<String> args) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            removeSong(event, args);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void removeSong(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            event.getChannel().sendMessageEmbeds(errorEmbed("You haven't specified which song to remove!", "").build()).queue();
            return;
        }

        if (!checkIsNumber(args.get(0))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(args.get(0) + " isn't a number!", "").build()).queue();
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();

        int choice = Integer.parseInt(args.get(0)) - 1;
        final List<AudioTrack> trackList = new ArrayList<>(songQueue);

        AudioTrackInfo removedSong;

        try {
            removedSong = trackList.get(choice).getInfo();

        } catch (Exception e) {
            event.getChannel().sendMessageEmbeds(errorEmbed("That number is out of bounds!", "").build()).queue();
            return;
        }

        trackList.remove(choice);

        songQueue.clear();

        for (AudioTrack track : trackList) {
            musicManager.getScheduler().queue(track);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Removed `" + removedSong.title + "` by `" + removedSong.author + "`");
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void skipToCommand(MessageReceivedEvent event, List<String> args) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (args.isEmpty() || !checkIsNumber(args.get(0))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You have not / you have not provided a valid number!",
                            "You must provide a whole number in seconds"
                    ).build()
            ).queue();
            return;
        }

        if (inSameVC(event)) {
            skipTo(event, args);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void skipTo(MessageReceivedEvent event, List<String> args) {

        if (!checkIsNumber(args.get(0))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You have not / you have not provided a valid number!",
                            ""
                    ).build()
            ).queue();
            return;
        }

        int target = Integer.parseInt(args.get(0));

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();

        if (songQueue.size() < target) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "The number you have entered is out of range!",
                            ""
                    ).build()
            ).queue();
            return;
        }

        for (int i = target; i > 0; i--) {
            musicManager.getAudioPlayer().stopTrack();

            if (!musicManager.getScheduler().getSongQueue().isEmpty()) {
                musicManager.getScheduler().nextTrack();
            }
        }

        showNowPlaying(event);
    }

    private void moveCommand(MessageReceivedEvent event, List<String> args) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            moveSong(event, args);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void moveSong(MessageReceivedEvent event, List<String> args) {
        if (args.size() < 2) {
            event.getChannel().sendMessageEmbeds(errorEmbed("This command requires 2 arguments!", "").build()).queue();
            return;
        }

        if (!checkIsNumber(args.get(0))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(args.get(0) + " isn't a number!", "").build()).queue();
            return;
        }

        if (!checkIsNumber(args.get(1))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(args.get(1) + " isn't a number!", "").build()).queue();
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> songQueue = musicManager.getScheduler().getSongQueue();

        if (songQueue.size() < 2) {
            event.getChannel().sendMessageEmbeds(errorEmbed("There aren't enough songs to move around!", "").build()).queue();
            return;
        }

        int choice = Integer.parseInt(args.get(0)) - 1;
        int newPosition = Integer.parseInt(args.get(1)) - 1;
        final List<AudioTrack> trackList = new ArrayList<>(songQueue);

        AudioTrack movedSong;

        try {
            movedSong = trackList.get(choice);

            trackList.remove(choice);
            trackList.add(newPosition, movedSong);

        } catch (Exception e) {
            event.getChannel().sendMessageEmbeds(errorEmbed("That number is out of bounds!", "").build()).queue();
            return;
        }

        songQueue.clear();

        for (AudioTrack track : trackList) {
            musicManager.getScheduler().queue(track);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setDescription("Moved `" + movedSong.getInfo().title + "` to position `" + (newPosition + 1) + "`");
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void removeDuplicatesCommand(MessageReceivedEvent event, List<String> args) {
        if (!userInVC(event)) {
            // user not in a VC
            event.getChannel().sendMessageEmbeds(errorEmbed(
                    "You are not in a Voice channel!",
                    "Join a voice channel before using this command!"
            ).build()).queue();
            return;
        }
        if (inSameVC(event)) {
            // user in same VC as bot
            removeDuplicates(event);
            return;
        } else {
            // user in different VC from bot
            event.getChannel().sendMessageEmbeds(errorEmbed(
                    "The music player is currently in a different VC!",
                    "You must be in the same VC to control the music player"
            ).build()).queue();
        }
        // user in VC and bot not in VC
        event.getChannel().sendMessageEmbeds(errorEmbed(
                "The music player is currently inactive!",
                ""
        ).build()).queue();
    }

    private void removeDuplicates(MessageReceivedEvent event) {
        Member member = event.getMember();

        if (!hasPermissions(Objects.requireNonNull(member))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You don't have the permissions to use this command!",
                            "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                    ).build()
            ).queue();
            return;
        }

        BlockingQueue<AudioTrack> songQueue = PlayerManager.getInstance().getMusicManager(event.getGuild()).getScheduler().getSongQueue();
        ArrayList<String> songs = new ArrayList<>();

        for (AudioTrack song : songQueue) {
            if (songs.contains(song.getIdentifier())) {
                songQueue.remove(song);

            } else {
                songs.add(song.getIdentifier());
            }
        }

        event.getMessage().addReaction("✅").queue();
    }

    private void pauseSongCommand(MessageReceivedEvent event, List<String> list) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            pauseSong(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void pauseSong(MessageReceivedEvent event) {
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).getAudioPlayer();
        audioPlayer.setPaused(!audioPlayer.isPaused());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (audioPlayer.isPaused()) {
            embed.setDescription("⏸ - The player is now paused");
        } else {
            embed.setDescription("▶ - The player is now playing");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void skipCommand(MessageReceivedEvent event, List<String> list) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                voteSkip(event, event.getGuild().getSelfMember().getVoiceState().getChannel());
                return;
            }
        }

        if (inSameVC(event)) {
            skipSong(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void voteSkip(MessageReceivedEvent event, AudioChannel voiceChannel) {

        MessageChannel channel = event.getChannel();
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrackInfo currentTrack = musicManager.getAudioPlayer().getPlayingTrack().getInfo();

        if (currentTrack == null) {
            event.getChannel().sendMessageEmbeds(errorEmbed("There is currently no song playing!", "").build()).queue();
            return;
        }

        int threshold = (voiceChannel.getMembers().size() / 2);

        for (Member member : voiceChannel.getMembers()) {
            if (member.getUser().isBot()) {
                threshold--;
            }
        }

        if (threshold == 1) {
            threshold = 2;
        }

        System.out.println(threshold);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);
        embed.setTitle("Vote to skip");
        embed.setDescription("Reactions needed to skip: " + (threshold + 1));

        int finalThreshold = threshold;
        channel.sendMessageEmbeds(embed.build()).queue(message -> {
            VoteMessage voteMessage = new VoteMessage(message, finalThreshold, () -> skipSong(event));

            voteMessage.activate(1);
        });
    }

    private void skipSong(MessageReceivedEvent event) {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrackInfo currentTrack = musicManager.getAudioPlayer().getPlayingTrack().getInfo();

        if (currentTrack == null) {
            event.getChannel().sendMessageEmbeds(errorEmbed("There is currently no song playing!", "").build()).queue();
            return;
        }

        musicManager.getAudioPlayer().stopTrack();

        if (!musicManager.getScheduler().getSongQueue().isEmpty()) {
            musicManager.getScheduler().nextTrack();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Skipped `" + currentTrack.title + "` by `" + currentTrack.author + "`");
        embed.setColor(ColorScheme.RESPONSE);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void shuffleCommand(MessageReceivedEvent event, List<String> list) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            shuffleQueue(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void shuffleQueue(MessageReceivedEvent event) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).getScheduler();
        scheduler.toggleShuffle();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isShuffle()) {
            embed.setDescription("\uD83D\uDD00 - Shuffle play is on");
        } else {
            embed.setDescription("shuffle is off");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void loopQueueCommand(MessageReceivedEvent event, List<String> list) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            loopQueue(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void loopQueue(MessageReceivedEvent event) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).getScheduler();
        scheduler.toggleLoop();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isLoopQueue()) {
            embed.setDescription("\uD83D\uDD01 - Loop Queue is on");
        } else {
            embed.setDescription("Loop queue is off");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void loopSongCommand(MessageReceivedEvent event, List<String> list) {
        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (inSameVC(event)) {
            loopSong(event);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void loopSong(MessageReceivedEvent event) {
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).getScheduler();
        scheduler.toggleLoopSong();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(ColorScheme.RESPONSE);

        if (scheduler.isLoopSong()) {
            embed.setDescription("\uD83D\uDD02 - Loop is on");
        } else {
            embed.setDescription("Loop is off");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void seekCommand(MessageReceivedEvent event, List<String> args) {

        Member member = event.getMember();

        if (inSameVC(event)) {
            int members = event.getMember().getVoiceState().getChannel().getMembers().size();
            if (!hasPermissions((member)) && members > 2) {
                event.getChannel().sendMessageEmbeds(errorEmbed(
                                "You don't have the permissions to use this command!",
                                "This command requires the `DJ` (case sensitive) role or a role with the 'Manage Channels' permission to use"
                        ).build()
                ).queue();
                return;
            }
        }

        if (args.isEmpty() || !checkIsNumber(args.get(0))) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You have not / you have not provided a valid number!",
                            "You must provide a whole number in seconds"
                    ).build()
            ).queue();
            return;
        }

        if (inSameVC(event)) {
            seekThrough(event, args);
        } else {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                            "You are not in a VC with the bot!",
                            ""
                    ).build()
            ).queue();
        }
    }

    private void seekThrough(MessageReceivedEvent event, List<String> args) {

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrack playingTrack = musicManager.getAudioPlayer().getPlayingTrack();

        int trackingAmount = Integer.parseInt(args.get(0));

        if (trackingAmount < 0 && playingTrack.getPosition() < Math.abs(trackingAmount)) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                    "The amount u have provided is out of range!",
                    "The current track is at: " + formatTime(playingTrack.getPosition())
            ).build()).queue();
            return;
        }

        if (trackingAmount > 0 && (playingTrack.getDuration() - playingTrack.getPosition()) < Math.abs(trackingAmount)) {
            event.getChannel().sendMessageEmbeds(errorEmbed(
                    "The amount u have provided is out of range!",
                    "The current track is at: " + formatTime(playingTrack.getPosition())
            ).build()).queue();
            return;
        }

        playingTrack.setPosition(playingTrack.getPosition() + TimeUnit.SECONDS.toMillis(trackingAmount));
        showNowPlaying(event);
    }

    private boolean checkIsNumber(String number) {
        try {
            int i = Integer.parseInt(number);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean userInVC(MessageReceivedEvent event) {
        return event.getMember().getVoiceState().inAudioChannel();
    }

    private boolean inSameVC(MessageReceivedEvent event) {
        AudioChannel userVoiceChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel selfVoiceChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if (userVoiceChannel == null || selfVoiceChannel == null) {
            return false;
        }

        return (userVoiceChannel == selfVoiceChannel);
    }

    private boolean isInVC(MessageReceivedEvent event) {
        return event.getGuild().getSelfMember().getVoiceState().inAudioChannel();
    }

    private boolean isVCEmpty(MessageReceivedEvent event) {
        Member self = event.getGuild().getSelfMember();
        AudioChannel channel = self.getVoiceState().getChannel();

        if (channel == null) {
            return true;
        }

        int members = 0;
        for (Member member : channel.getMembers()) {
            if (member.getUser().isBot()) {
                continue;
            }
            members++;
        }

        return (members == 0);
    }

    private boolean isURL(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private String formatTime(long duration) {

        Duration time = Duration.ofMillis(duration);
        long seconds = time.toSeconds();

        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;

        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    private boolean hasPermissions(Member user) {
        List<Role> roles = user.getRoles();

        if (user.isOwner()) {
            return true;
        }

        for (Role role : roles) {
            if (role.getName().equals("DJ")) {
                return true;
            }
        }

        return (user.hasPermission(Permission.MANAGE_CHANNEL) || user.hasPermission(Permission.ADMINISTRATOR));
    }

    private String timeLine(long currentTime, long duration) {
        ArrayList<String> line = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            line.add("=");
        }

        double percent = ((double) currentTime / duration) * 100;
        int index = Math.toIntExact(Math.round(percent) / 5);

        line.set(index, "\uD83D\uDD18");

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (String item : line) {
            sb.append(item);
        }

        sb.append("]");

        return sb.toString();
    }

    private EmbedBuilder errorEmbed(String errorMessage, String footerText) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription(errorMessage);
        embed.setFooter(footerText);
        embed.setColor(ColorScheme.ERROR);
        return embed;
    }
}