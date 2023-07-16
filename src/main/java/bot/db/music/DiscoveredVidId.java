package bot.db.music;

import bot.Bot;
import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.filters.Filters;

@Entity("DiscoveredVidId")
public class DiscoveredVidId {

    @Id
    private String spotifyId;
    private String youtubeId;
    private String title;

    public static String getYoutubeIdFromSpotifyId(String spotifyID) {
        Datastore datastore = Bot.getINSTANCE().getDatastore();
        DiscoveredVidId spotifyId = datastore.find(DiscoveredVidId.class)
                .filter(Filters.eq("spotifyId", spotifyID))
                .iterator().tryNext();

        if (spotifyId == null) {
            return null;
        }

        return spotifyId.getYoutubeId();
    }

    public DiscoveredVidId() {}

    public DiscoveredVidId(String spotifyID, String youtubeID, String title) {
        this.spotifyId = spotifyID;
        this.youtubeId = youtubeID;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }
}
