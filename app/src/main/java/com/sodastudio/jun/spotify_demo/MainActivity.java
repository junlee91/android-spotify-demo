package com.sodastudio.jun.spotify_demo;

import android.content.Intent;
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

    //   ____                _              _
    //  / ___|___  _ __  ___| |_ __ _ _ __ | |_ ___
    // | |   / _ \| '_ \/ __| __/ _` | '_ \| __/ __|
    // | |__| (_) | | | \__ \ || (_| | | | | |_\__ \
    //  \____\___/|_| |_|___/\__\__,_|_| |_|\__|___/
    //

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "5de6930c8a744270851a5064c7ff6333";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "http://localhost:8888/callback";

    /**
     * Request code that will be passed together with authentication result to the onAuthenticationResult
     */
    private static final int REQUEST_CODE = 1337;

    private static final String TAG = "MainActivity";

    //  _____ _      _     _
    // |  ___(_) ___| | __| |___
    // | |_  | |/ _ \ |/ _` / __|
    // |  _| | |  __/ | (_| \__ \
    // |_|   |_|\___|_|\__,_|___/
    //

    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;

    private Button mPlayButton;
    private Button mLoginButton;

    private String AUTH_TOKEN;

    private SpotifyService spotifyService;

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

    //  ___       _ _   _       _ _          _   _
    // |_ _|_ __ (_) |_(_) __ _| (_)______ _| |_(_) ___  _ __
    //  | || '_ \| | __| |/ _` | | |_  / _` | __| |/ _ \| '_ \
    //  | || | | | | |_| | (_| | | |/ / (_| | |_| | (_) | | | |
    // |___|_| |_|_|\__|_|\__,_|_|_/___\__,_|\__|_|\___/|_| |_|
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set views
        mPlayButton = (Button)findViewById(R.id.play_music);
        mLoginButton = (Button)findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(mListener);

        updateView();
    }


    View.OnClickListener mListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.login_button:
                    onLoginButtonClicked();
                    break;

                case R.id.play_music:
                    break;
            }
        }
    };


    //     _         _   _                _   _           _   _
    //    / \  _   _| |_| |__   ___ _ __ | |_(_) ___ __ _| |_(_) ___  _ __
    //   / _ \| | | | __| '_ \ / _ \ '_ \| __| |/ __/ _` | __| |/ _ \| '_ \
    //  / ___ \ |_| | |_| | | |  __/ | | | |_| | (_| (_| | |_| | (_) | | | |
    // /_/   \_\__,_|\__|_| |_|\___|_| |_|\__|_|\___\__,_|\__|_|\___/|_| |_|
    //

    private void openLoginWindow() {

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN,REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE)
        {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG,"Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG,"Auth result: " + response.getType());
            }
        }
    }

    private void onAuthenticationComplete(final AuthenticationResponse response) {

        Log.d(TAG,"Got authentication token");

        AUTH_TOKEN = response.getAccessToken();

        if(mPlayer == null)
        {
            Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);

            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    Log.d(TAG,"-- Player initialized --");
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addNotificationCallback(MainActivity.this);

                    Log.d("MainActivity", "AccessToken: " + response.getAccessToken());

                    // Trigger UI refresh
                    updateView();
                    // Set API
                    setServiceAPI();
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        } else {
            mPlayer.login(response.getAccessToken());
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

    public void onLoginButtonClicked() {
        if (!isLoggedIn()) {
            Log.d(TAG, "Logging in");
            openLoginWindow();
        } else {
            mPlayer.logout();
        }
    }

    //   ____      _ _ _                _      __  __      _   _               _
    //  / ___|__ _| | | |__   __ _  ___| | __ |  \/  | ___| |_| |__   ___   __| |___
    // | |   / _` | | | '_ \ / _` |/ __| |/ / | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
    // | |__| (_| | | | |_) | (_| | (__|   <  | |  | |  __/ |_| | | | (_) | (_| \__ \
    //  \____\__,_|_|_|_.__/ \__,_|\___|_|\_\ |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
    //

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
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

}

