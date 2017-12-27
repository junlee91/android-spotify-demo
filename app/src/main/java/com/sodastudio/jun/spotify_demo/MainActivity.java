package com.sodastudio.jun.spotify_demo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
    implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "5de6930c8a744270851a5064c7ff6333";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";

    private static final int REQUEST_CODE = 1337;

    private static final String TAG = "MainActivity";

    private Player mPlayer;

    private Button mPlayButton;

    private static String TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN,REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        mPlayButton = (Button)findViewById(R.id.play_music);


    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE)
        {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);

                        Log.d("MainActivity", "AccessToken: " + response.getAccessToken());

                        TOKEN = response.getAccessToken();

                        SpotifyApi api = new SpotifyApi();

                        api.setAccessToken(TOKEN);

                        SpotifyService spotifyService = api.getService();

                        spotifyService.searchTracks("Maroon 5", new SpotifyCallback<TracksPager>() {
                            @Override
                            public void failure(SpotifyError spotifyError) {
                                Log.d(TAG, "failed.. " + spotifyError.toString());
                            }

                            @Override
                            public void success(TracksPager tracksPager, Response response) {
                                                // artist: Maroon 5
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(0).uri);              // music link
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(0).name);             // title
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(0).album.name);       // album name
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(0).album.images.get(0).url);  // album image
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(0).duration_ms);          // song duration
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(0).artists.get(0).name); // artists


                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(1).uri);              // music link
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(1).name);             // title
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(1).album.name);       // album name
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(1).album.images.get(0).url);  // album image
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(1).duration_ms);          // song duration
                                Log.d(TAG, "success! " + tracksPager.tracks.items.get(1).artists.get(0).name); // artists
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());

        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.playUri(null, "spotify:track:4nYsmWkuTaowTMy4gskmBw", 0, 0);

            }
        });

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}

























