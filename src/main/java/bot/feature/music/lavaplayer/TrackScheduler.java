//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bot.feature.music.lavaplayer;

import bot.db.music.DiscoveredVidId;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrackInfo> songQueue;
    private final HashMap<AudioTrackInfo, AudioTrack> songSet;
    private final HashMap<AudioTrackInfo, Member> songRequesters;
    private final HashMap<AudioTrackInfo, Thread> queryThreads;
    private final Guild guild;
    private boolean loopQueue = false;
    private boolean shuffle = false;
    private boolean loopSong = false;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.songQueue = new LinkedBlockingQueue<>();
        this.songSet = new HashMap<>();
        this.songRequesters = new HashMap<>();
        this.queryThreads = new HashMap<>();
        this.guild = guild;
    }

    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.loopSong) {
            this.player.startTrack(track.makeClone(), false);
        } else {
            if (this.loopQueue) {
                AudioTrack repeatedTrack = track.makeClone();
                this.queue(repeatedTrack, (Member) track.getUserData());
            }

            if (endReason.mayStartNext) {
                this.nextTrack();
            }
        }
    }

    public void nextTrack() {
        if (this.shuffle) {
            this.shufflePlay();
        } else {
            AudioTrackInfo nextTrack = this.songQueue.poll();
            Thread thread = this.queryThreads.get(nextTrack);
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            }

            this.queue(this.songSet.get(nextTrack), this.songRequesters.get(nextTrack));
        }
    }

    private void shufflePlay() {
        Random r = new Random();
        ArrayList<AudioTrackInfo> trackList = new ArrayList(this.songQueue);
        AudioTrackInfo choice = trackList.get(r.nextInt(this.songQueue.size()) - 1);
        this.songQueue.remove(choice);
        this.queue(this.songSet.get(choice), this.songRequesters.get(choice));
        this.songSet.remove(choice);
    }

    public void queue(AudioTrack track, Member member) {
        track.setUserData(member);
        if (!this.player.startTrack(track, true)) {
            this.songQueue.offer(track.getInfo());
            this.songSet.put(track.getInfo(), track);
            this.songRequesters.put(track.getInfo(), member);
        } else {
            this.songSet.remove(track.getInfo());
        }
    }

    public void queue(Track track, Member member) {
        AudioTrackInfo trackInfo = this.trackInfoFromSpotifyTrack(track);
        this.songQueue.offer(trackInfo);
        this.songRequesters.put(trackInfo, member);
        Thread queryThread = this.buildYTQuery(trackInfo, member);
        queryThread.start();
        this.queryThreads.put(trackInfo, queryThread);
        if (this.player.getPlayingTrack() == null) {
            try {
                queryThread.join();
                this.nextTrack();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public BatchQueueJob queue(Album album, Member member) {
        BatchQueueJob job = new BatchQueueJob();
        for (TrackSimplified track : album.getTracks().getItems()) {
            AudioTrackInfo trackInfo = trackInfoFromSpotifyTrack(track);
            this.songQueue.offer(trackInfo);
            this.songRequesters.put(trackInfo, member);

            Thread thread = job.submitTrack(trackInfo, member, this);

            if (player.getPlayingTrack() == null) {
                try {
                    thread.join();
                    nextTrack();

                } catch (InterruptedException ignored) {
                }
            }
        }
        return job;
    }

    public BatchQueueJob queue(Playlist playlist, Member member) {
        BatchQueueJob job = new BatchQueueJob();
        for (PlaylistTrack plTrack : playlist.getTracks().getItems()) {
            Track track = (Track) plTrack.getTrack();
            AudioTrackInfo trackInfo = trackInfoFromSpotifyTrack(track);
            this.songQueue.offer(trackInfo);
            this.songRequesters.put(trackInfo, member);

            Thread thread = job.submitTrack(trackInfo, member, this);

            if (player.getPlayingTrack() == null) {
                try {
                    thread.join();
                    nextTrack();

                } catch (InterruptedException ignored) {
                }
            }
        }
        return job;
    }



    public void endQuery(AudioTrackInfo trackInfo, AudioTrack audioTrack) {
        this.queryThreads.remove(trackInfo);
        replaceTrackInfo(trackInfo, audioTrack.getInfo());
        this.songSet.put(audioTrack.getInfo(), audioTrack);
    }

    private Thread buildYTQuery(AudioTrackInfo trackInfo, Member member) {
        return new Thread(() -> {
            String ytId = DiscoveredVidId.getYoutubeIdFromSpotifyId(trackInfo.identifier);
            String identifier;
            if (ytId != null) {
                identifier = "https://youtu.be/" + ytId;
            } else {
                identifier = "ytsearch:" + trackInfo.author + "- " + trackInfo.title;
            }

            AudioTrack audioTrack = PlayerManager.getInstance().loadTrack(identifier);
            audioTrack.setUserData(member);

            synchronized (this) {
                this.endQuery(trackInfo, audioTrack);
            }
        });
    }

    private String buildArtistString(ArtistSimplified[] artists) {
        StringBuilder sb = new StringBuilder();
        int numArtists = artists.length;

        for (int i = 0; i < numArtists; ++i) {
            ArtistSimplified artist = artists[i];
            sb.append(artist.getName());
            sb.append(" ");
        }

        return sb.toString();
    }

    private AudioTrackInfo trackInfoFromSpotifyTrack(Track track) {
        return new AudioTrackInfo(track.getName(), this.buildArtistString(track.getArtists()), (long) track.getDurationMs(), track.getId(), false, track.getUri());
    }

    private AudioTrackInfo trackInfoFromSpotifyTrack(TrackSimplified track) {
        return new AudioTrackInfo(track.getName(), this.buildArtistString(track.getArtists()), (long) track.getDurationMs(), track.getId(), false, track.getUri());
    }

    public int getPositionOfTrack(AudioTrack track) {
        ArrayList<AudioTrackInfo> songs = new ArrayList(this.songQueue);
        return songs.indexOf(track.getInfo());
    }

    public long getTimeUntilTrack(AudioTrack track) {
        ArrayList<AudioTrackInfo> songs = new ArrayList(this.songQueue);
        long position = songs.indexOf(track.getInfo());
        long timeUntilSong = 0L;

        for (int i = 0; (long) i < position; ++i) {
            timeUntilSong += this.songSet.get(songs.get(i)).getDuration();
        }

        AudioTrack currentTrack = this.player.getPlayingTrack();
        if (currentTrack != null) {
            timeUntilSong += currentTrack.getDuration() - currentTrack.getPosition();
            ++position;
        }

        return timeUntilSong;
    }

    public BlockingQueue<AudioTrackInfo> getSongQueue() {
        return this.songQueue;
    }

    public HashMap<AudioTrackInfo, AudioTrack> getSongSet() {
        return this.songSet;
    }

    public HashMap<AudioTrackInfo, Member> getSongRequesters() {
        return this.songRequesters;
    }

    public void toggleLoop() {
        this.loopQueue = !this.loopQueue;
    }

    public void toggleShuffle() {
        this.shuffle = !this.shuffle;
    }

    public boolean isLoopQueue() {
        return this.loopQueue;
    }

    public boolean isShuffle() {
        return this.shuffle;
    }

    public void toggleLoopSong() {
        this.loopSong = !this.loopSong;
    }

    public boolean isLoopSong() {
        return this.loopSong;
    }

    public void replaceTrackInfo(AudioTrackInfo trackInfo1, AudioTrackInfo trackInfo2) {
        ArrayList<AudioTrackInfo> trackList = new ArrayList<>(songQueue);
        if (trackList.contains(trackInfo1)) {
            trackList.set(trackList.indexOf(trackInfo1), trackInfo2);
        }

        Member member = songRequesters.get(trackInfo1);
        songRequesters.remove(trackInfo1);
        songRequesters.put(trackInfo2, member);

        songQueue.clear();
        for (AudioTrackInfo trackInfo : trackList) {
            songQueue.offer(trackInfo);
        }
    }
}
