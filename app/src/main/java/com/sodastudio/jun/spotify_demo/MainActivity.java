package com.sodastudio.jun.spotify_demo;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sodastudio.jun.spotify_demo.ui.MainFragment;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
    implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    private static final String TAG = "Spotify MainActivity";

    //  _____ _      _     _
    // |  ___(_) ___| | __| |___
    // | |_  | |/ _ \ |/ _` / __|
    // |  _| | |  __/ | (_| \__ \
    // |_|   |_|\___|_|\__,_|___/
    //

    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;

    private Toast mToast;

    private String AUTH_TOKEN;

    public static SpotifyService spotifyService;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "Ok");
        }

        @Override
        public void onError(Error error) {
            Log.e(TAG, "Error: " + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();

        AUTH_TOKEN = getIntent().getStringExtra(SpotifyLoginActivity.AUTH_TOKEN);

        onAuthenticationComplete(AUTH_TOKEN);
        updateView();
    }

    private void onAuthenticationComplete(final String auth_token) {

        Log.d(TAG,"Got authentication token");

        if(mPlayer == null)
        {
            Config playerConfig = new Config(this, auth_token, SpotifyLoginActivity.CLIENT_ID);

            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    Log.d(TAG,"-- Player initialized --");
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addNotificationCallback(MainActivity.this);

                    Log.d(TAG, "AccessToken: " + auth_token);

                    // Trigger UI refresh
                    updateView();
                    // Set API
                    setServiceAPI();
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                }
            });
        } else {
            mPlayer.login(auth_token);
        }

    }

    private void setServiceAPI(){
        Log.d(TAG, "Setting Spotify API Service");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(AUTH_TOKEN);

        spotifyService = api.getService();
    }

    /*
    *
    * TOKEN = response.getAccessToken();

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
    *
    *
    * */


    //  _   _ ___   _____                 _
    // | | | |_ _| | ____|_   _____ _ __ | |_ ___
    // | | | || |  |  _| \ \ / / _ \ '_ \| __/ __|
    // | |_| || |  | |___ \ V /  __/ | | | |_\__ \
    //  \___/|___| |_____| \_/ \___|_| |_|\__|___/
    //

    private void updateView() {
        boolean loggedIn = isLoggedIn();


    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }


    private void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    //   ____      _ _ _                _      __  __      _   _               _
    //  / ___|__ _| | | |__   __ _  ___| | __ |  \/  | ___| |_| |__   ___   __| |___
    // | |   / _` | | | '_ \ / _` |/ __| |/ / | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
    // | |__| (_| | | | |_) | (_| | (__|   <  | |  | |  __/ |_| | | | (_) | (_| \__ \
    //  \____\__,_|_|_|_.__/ \__,_|\___|_|\_\ |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
    //

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "User logged in");
        //showToast("Login Success!");
    }

    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d(TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);
    }

    //  ____            _                   _   _
    // |  _ \  ___  ___| |_ _ __ _   _  ___| |_(_) ___  _ __
    // | | | |/ _ \/ __| __| '__| | | |/ __| __| |/ _ \| '_ \
    // | |_| |  __/\__ \ |_| |  | |_| | (__| |_| | (_) | | | |
    // |____/ \___||___/\__|_|   \__,_|\___|\__|_|\___/|_| |_|
    //

    @Override
    protected void onPause() {
        super.onPause();

        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(MainActivity.this);
            mPlayer.removeConnectionStateCallback(MainActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d(TAG, "Playback event received: " + playerEvent.name());

        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d(TAG, "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

}

