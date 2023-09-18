package bot.db.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

@Entity("PlayList")
public class Playlist {

    @Id
    private String listId;
    private String userId;
    private String playlistName;
    private ArrayList<String> videoIds;


    public Playlist(){}

    public Playlist(User user, String playlistName) {
        this.userId = user.getId();
        this.listId = String.valueOf((userId + playlistName).hashCode());
        this.playlistName = playlistName;
        this.videoIds = new ArrayList<>();
    }

    public Playlist(User user, String playlistName, ArrayList<AudioTrackInfo> tracks) {
        this.userId = user.getId();
        this.listId = String.valueOf((userId + playlistName).hashCode());
        this.playlistName = playlistName;
        this.videoIds = buildTrackList(tracks);
    }

    private ArrayList<String> buildTrackList(ArrayList<AudioTrackInfo> tracks) {
        ArrayList<String> trackList = new ArrayList<>();
        for (AudioTrackInfo trackInfo : tracks) {
            trackList.add(trackInfo.identifier);
        }
        return trackList;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void addSong(AudioTrack track) {
        videoIds.add(track.getInfo().identifier);
    }

    public ArrayList<String> getVideoIds() {
        return videoIds;
    }
}
