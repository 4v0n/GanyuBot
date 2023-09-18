package bot.feature.music.lavaplayer;

import bot.db.music.DiscoveredVidId;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;

public class BatchQueueJob {
    private final HashMap<AudioTrackInfo, Thread> executingThreads;
    private final ArrayList<AudioTrackInfo> trackInfoInOrder;
    private final ArrayList<AudioTrackInfo> tracksNotFound;

    private boolean isTerminated;

    public BatchQueueJob() {
        this.executingThreads = new HashMap<>();
        this.trackInfoInOrder = new ArrayList<>();
        this.tracksNotFound = new ArrayList<>();
        isTerminated = false;
    }

    public Thread submitTrack(AudioTrackInfo trackInfo, Member member, TrackScheduler scheduler) {
        trackInfoInOrder.add(trackInfo);
        tracksNotFound.add(trackInfo);

        Thread thread = new Thread(() -> {
            String ytId = DiscoveredVidId.getYoutubeIdFromSpotifyId(trackInfo.identifier);
            String identifier;
            if (ytId != null) {
                identifier = "https://youtu.be/" + ytId;
            } else {
                identifier = "ytsearch:" + trackInfo.author + "- " + trackInfo.title;
            }

            AudioTrack audioTrack = PlayerManager.getInstance().loadTrack(identifier);

            synchronized (this) {
                if (audioTrack == null) {
                    trackInfoInOrder.remove(trackInfo);
                } else {
                    audioTrack.setUserData(member);
                    tracksNotFound.remove(trackInfo);
                }
            }

            synchronized (scheduler) {
                scheduler.endQuery(trackInfo, audioTrack);
            }
        });

        executingThreads.put(trackInfo, thread);
        thread.start();

        return thread;
    }

    private void terminate() {
        for (Thread thread : executingThreads.values()) {

            if (!thread.isAlive()) {
                continue;
            }

            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public ArrayList<AudioTrackInfo> getTrackInfoInOrder() {
        if (!isTerminated) {
            terminate();
        }
        return trackInfoInOrder;
    }

    public ArrayList<AudioTrackInfo> getTracksNotFound() {
        if (!isTerminated) {
            terminate();
        }
        return tracksNotFound;
    }
}
