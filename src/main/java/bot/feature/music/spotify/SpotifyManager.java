package bot.feature.music.spotify;

import net.dv8tion.jda.api.entities.User;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class SpotifyManager {

    private static SpotifyManager INSTANCE;
    private static HashMap<String, SpotifyManager> LOGGED_IN_INSTANCES = new HashMap<>();

    private SpotifyApi spotifyApi;
    private ClientCredentialsRequest clientCredentialsRequest;
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

    protected SpotifyManager() {
    }

    public static SpotifyManager getInstance(User user) {

        if (INSTANCE == null) {
            INSTANCE = new SpotifyManager();
        }

        if (INSTANCE.spotifyApi != null) {
            INSTANCE.authenticateClient();
        }

        SpotifyManager loggedInInstance = attemptLoadUser(user);
        if (loggedInInstance != null) {
            return loggedInInstance;
        }

        return INSTANCE;
    }

    public static SpotifyManager getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new SpotifyManager();
        }

        if (INSTANCE.spotifyApi != null) {
            INSTANCE.authenticateClient();
        }

        return INSTANCE;
    }

    private static SpotifyManager attemptLoadUser(User user) {
        return LOGGED_IN_INSTANCES.get(user.getId());
    }

    public static LoggedInSpotifyManager logInUser(User user, String authCode) {
        LoggedInSpotifyManager lsm = new LoggedInSpotifyManager(user);
        lsm.setup(authCode);
        LOGGED_IN_INSTANCES.put(user.getId(), lsm);
        return lsm;
    }

    public URI getauthCodeUri() {
        return authorizationCodeUriRequest.execute();
    }

    public void setup(HashMap<String, String> settings) {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(settings.get("SPOTIFY_CLIENT_ID"))
                .setClientSecret(settings.get("SPOTIFY_CLIENT_SECRET"))
                .setRedirectUri(SpotifyHttpManager.makeUri(settings.get("REDIRECT_URL")))
                .build();
        this.clientCredentialsRequest = spotifyApi.clientCredentials().build();
        this.authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().build();
    }

    private void authenticateClient() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Album getAlbum(String albumURL) {
        try {
            URL url = new URL(albumURL);
            String songId = url.getPath().substring(7);
            GetAlbumRequest gar = spotifyApi.getAlbum(songId).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Playlist getPlaylist(String playlistURL) {
        try {
            URL url = new URL(playlistURL);
            String playlistId = url.getPath().substring(10);
            GetPlaylistRequest gar = spotifyApi.getPlaylist(playlistId).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Paging<PlaylistTrack> getPLaylistItems(String playlistURL) {
        try {
            URL url = new URL(playlistURL);
            String playlistId = url.getPath().substring(10);
            GetPlaylistsItemsRequest gar = spotifyApi.getPlaylistsItems(playlistId).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Track getSong(String songURL) {
        try {
            URL url = new URL(songURL);
            String songId = url.getPath().substring(7);
            GetTrackRequest gar = spotifyApi.getTrack(songId).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Track getSongById(String id) {
        try {
            GetTrackRequest gar = spotifyApi.getTrack(id).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    public static void removeUser(User user) {
        LOGGED_IN_INSTANCES.remove(user.getId());
    }
}
