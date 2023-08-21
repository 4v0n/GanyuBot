package bot.feature.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class manages and schedules audio tracks
 * <p>
 * Based on MenuDocs' implementation
 *
 * @author Aron Navodh Kumarawatta
 * @version 08.10.2022
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> songQueue;

    private final Guild guild;

    private boolean loopQueue = false;
    private boolean shuffle = false;
    private boolean loopSong = false;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.songQueue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        if (loopSong) {
            this.player.startTrack(track.makeClone(), false);
            return;
        }

        if (loopQueue) {
//            AudioTrackInfo trackInfo = track.getInfo();
//            AudioTrack newTrack = PlayerManager.getInstance().loadTrack(trackInfo.uri);
//            this.queue(newTrack);
            queue(track.makeClone());
        }

        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public void nextTrack() {
        if (shuffle) {
            shufflePlay();
        } else {
            queue(this.songQueue.poll());
        }
    }

    private void shufflePlay() {
        Random r = new Random();
        ArrayList<AudioTrack> trackList = new ArrayList<>(songQueue);
        AudioTrack choice = trackList.get(r.nextInt(songQueue.size()) - 1);
        songQueue.remove(choice);
        queue(choice);
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.songQueue.offer(track);
        }
    }

    public int getPositionOfTrack(AudioTrack track) {
        ArrayList<AudioTrack> songs = new ArrayList<>(songQueue);
        return songs.indexOf(track);
    }

    public long getTimeUntilTrack(AudioTrack track) {
        ArrayList<AudioTrack> songs = new ArrayList<>(songQueue);
        long position = songs.indexOf(track);

        long timeUntilSong = 0;


        for (int i = 0; i < position; i++){
            timeUntilSong = timeUntilSong + songs.get(i).getDuration();
        }

        AudioTrack currentTrack = player.getPlayingTrack();
        if (currentTrack != null){
            timeUntilSong = timeUntilSong + ( currentTrack.getDuration() - currentTrack.getPosition() );
            position++;
        }
        return timeUntilSong;
    }

    public BlockingQueue<AudioTrack> getSongQueue() {
        return songQueue;
    }

    public void toggleLoop() {
        loopQueue = !loopQueue;
    }

    public void toggleShuffle() {
        shuffle = !shuffle;
    }

    public boolean isLoopQueue() {
        return loopQueue;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void toggleLoopSong() {
        loopSong = !loopSong;
    }

    public boolean isLoopSong() {
        return loopSong;
    }
}
