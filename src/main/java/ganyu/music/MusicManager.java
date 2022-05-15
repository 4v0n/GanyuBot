package ganyu.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import ganyu.music.lavaplayer.AudioPlayerSentHandler;
import ganyu.music.lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;

/**
 * This manages music
 */
public class MusicManager {
    private final AudioPlayer audioPlayer;
    private final TrackScheduler scheduler;
    private final AudioPlayerSentHandler sentHandler;

    public MusicManager(AudioPlayerManager manager, Guild guild) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer, guild);
        this.audioPlayer.addListener(this.scheduler);
        this.sentHandler = new AudioPlayerSentHandler(this.audioPlayer);
    }

    public AudioPlayerSentHandler getSentHandler() {
        return sentHandler;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }
}
