package ganyu.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import ganyu.music.MusicManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class manages and schedules audio tracks
 *
 * Based on MenuDocs' implementation
 *
 * @author Aron Navodh Kumarawatta
 * @version 15.05.2022
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

        if (loopSong){
            this.player.startTrack(track.makeClone(), false);
            return;
        }

        if (loopQueue){
            AudioTrackInfo trackInfo = track.getInfo();
            PlayerManager.getInstance().reQueue(guild, trackInfo.uri);
        }

        if (endReason.mayStartNext) {
            nextTrack();
        }
    }


    public void nextTrack() {
        if (shuffle){
            shufflePlay();
        } else {
            queue(this.songQueue.poll());
        }

        AudioChannel channel = guild.getAudioManager().getConnectedChannel();

        if (songQueue.isEmpty() || channel.getMembers().size() == 1){
            Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    // ignore
                }
            };

            Thread waitThread = new Thread() {
              public void run(){
                  try {
                      Thread.sleep(TimeUnit.MINUTES.toMillis(1));
                      if (songQueue.isEmpty() || channel.getMembers().size() == 1) {
                          guild.getAudioManager().closeAudioConnection();
                          MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
                          musicManager.getScheduler().songQueue.clear();
                          musicManager.getAudioPlayer().stopTrack();

                      }
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
            };

            waitThread.setUncaughtExceptionHandler(exceptionHandler);
            waitThread.start();
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

    public AudioPlayer getPlayer() {
        return player;
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

    public AudioTrack getCurrentlyPlaying() {
        return player.getPlayingTrack();
    }

    public void toggleLoopSong() {
        loopSong = !loopSong;
    }

    public boolean isLoopSong() {
        return loopSong;
    }
}
