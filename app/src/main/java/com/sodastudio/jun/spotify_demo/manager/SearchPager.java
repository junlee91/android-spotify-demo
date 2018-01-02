package com.sodastudio.jun.spotify_demo.manager;

import android.content.Context;
import android.util.Log;

import com.sodastudio.jun.spotify_demo.MainActivity;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

/**
 * Created by jun on 12/29/17.
 */

public class SearchPager {

    private static SearchPager searchPager;

    private SpotifyService spotifyService = MainActivity.spotifyService;

    public interface CompleteListener {
        void onComplete(List<Track> items);
        void onError(Throwable error);
    }
    public interface ArtistListener{
        void onComplete(String url);
        void onError(Throwable error);
    }

    public static SearchPager getInstance(Context context){
        if(searchPager == null){
            searchPager = new SearchPager();
        }

        return searchPager;
    }

    public void getTracksFromSearch(String query, CompleteListener listener){
        getData(query, listener);
    }

    private void getData(String query, final CompleteListener listener){

        spotifyService.searchTracks(query, new SpotifyCallback<TracksPager>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                listener.onError(spotifyError);
            }

            @Override
            public void success(TracksPager tracksPager, Response response) {
                listener.onComplete(tracksPager.tracks.items);
            }
        });
    }

    public void getArtist(String id, final ArtistListener listener){

        spotifyService.getArtist(id, new SpotifyCallback<Artist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("SearchPager", spotifyError.toString());
                listener.onError(spotifyError);
            }

            @Override
            public void success(Artist artist, Response response) {
                Log.d("SearchPager CHECK", artist.images.get(0).url); // img url
                listener.onComplete(artist.images.get(0).url);
            }
        });
    }

}
