package bot.feature.music.spotify;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class LoggedInSpotifyManager extends SpotifyManager{

    private final User user;
    private SpotifyApi spotifyApi;
    private AuthorizationCodeRequest authorizationCodeRequest;

    LoggedInSpotifyManager(User user) {
        super();
        this.user = user;
    }

    public void setup(String authCode) {
        this.authorizationCodeRequest = SpotifyManager.getInstance().getSpotifyApi().authorizationCode(authCode).build();
        authUser();
    }

    private void authUser() {
        try {
            SpotifyApi parent = SpotifyManager.getInstance().getSpotifyApi();

            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            this.spotifyApi = new SpotifyApi.Builder()
                    .setClientId(parent.getClientId())
                    .setClientSecret(parent.getClientSecret())
                    .setRedirectUri(parent.getRedirectURI())
                    .build();

            this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            this.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            startRefresher();

        } catch (IOException | SpotifyWebApiException | ParseException ignored) {
        }

    }

    private void startRefresher() {
        Thread thread = new Thread(() -> {
            boolean error = false;
            while (!error) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(3500));
                } catch (InterruptedException e) {
                    error = true;
                    e.printStackTrace();
                }

                AuthorizationCodeRefreshRequest acrr = this.spotifyApi.authorizationCodeRefresh().build();
                try {
                    AuthorizationCodeCredentials acc = acrr.execute();
                    this.spotifyApi.setAccessToken(acc.getAccessToken());

                } catch (IOException | SpotifyWebApiException | ParseException e) {
                    error = true;
                    e.printStackTrace();
                }
            }
            SpotifyManager.getInstance().removeUser(user);

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Spotify account unlinked!")
                        .setDescription("Your account has been unlinked due an error")
                        .setFooter("You may simply re-link your account");
                privateChannel.sendMessageEmbeds(embed.build()).queue();
            });
        });
        thread.start();
    }

    @Override
    public Playlist getPlaylist(String playlistURL) {
        try {
            URL url = new URL(playlistURL);
            String playlistId = url.getPath().substring(10);
            GetPlaylistRequest gar = this.spotifyApi.getPlaylist(playlistId).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Paging<PlaylistTrack> getPLaylistItems(String playlistURL) {
        System.out.println("here");
        try {
            URL url = new URL(playlistURL);
            String playlistId = url.getPath().substring(10);
            GetPlaylistsItemsRequest gar = this.spotifyApi.getPlaylistsItems(playlistId).build();
            return gar.execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SpotifyApi getSpotifyApi() {
        return this.spotifyApi;
    }
}
