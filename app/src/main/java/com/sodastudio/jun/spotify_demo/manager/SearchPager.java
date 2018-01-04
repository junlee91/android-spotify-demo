package com.sodastudio.jun.spotify_demo.manager;

import android.content.Context;
import android.util.Log;

import com.sodastudio.jun.spotify_demo.MainActivity;
import com.sodastudio.jun.spotify_demo.model.AlbumNew;
import com.sodastudio.jun.spotify_demo.model.SimplePlaylist;
import com.sodastudio.jun.spotify_demo.model.TopArtist;
import com.sodastudio.jun.spotify_demo.model.TopTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.NewReleases;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
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
    public interface onCompleteListener {
        void onComplete();
        void onError(Throwable error);
    }
    public interface onCompleteTopArtistListener {
        void onComplete();
        void onError(Throwable error);
    }
    public interface onCompleteTopTrackListener {
        void onComplete();
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
                //Log.d("SearchPager CHECK", artist.images.get(0).url); // img url
                listener.onComplete(artist.images.get(1).url);
            }
        });
    }

    public void getMyTopArtist(final onCompleteTopArtistListener listener){

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 10);

        final ListManager listManager = ListManager.getInstance();

        spotifyService.getTopArtists(options, new SpotifyCallback<Pager<Artist>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("SearchPager", spotifyError.toString());

                if(listener != null)
                    listener.onError(spotifyError);
            }

            @Override
            public void success(Pager<Artist> artistPager, Response response) {
                List<Artist> mList = artistPager.items;

                for(Artist art : mList){
                    Log.d("SearchPager", art.name);
                    Log.d("SearchPager", art.images.get(1).url);

                    listManager.addTopArtist(new TopArtist(art.name, art.images.get(1).url));
                }

                if(listener != null)
                    listener.onComplete();
                else{
                    Log.d("SearchPager", "What is happening?");
                }
            }
        });
    }

    public void getMyTopTracks(final onCompleteTopTrackListener listener){
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 10);

        final ListManager listManager = ListManager.getInstance();

        spotifyService.getTopTracks(options, new SpotifyCallback<Pager<Track>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("SearchPager", spotifyError.toString());

                if(listener != null)
                    listener.onError(spotifyError);
            }

            @Override
            public void success(Pager<Track> trackPager, Response response) {
                List<Track> tracks = trackPager.items;

                for(Track track : tracks){
                    Log.d("SearchPager", track.album.name);
                    Log.d("SearchPager", track.album.images.get(1).url);

                    listManager.addTopTrack(new TopTrack(track.album.name, track.album.images.get(1).url));

                }

                if(listener != null)
                    listener.onComplete();
            }
        });
    }

    public void getNewRelease(final onCompleteListener listener){
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, 0);  // 0 ~ 5 6 ~ 10
        options.put(SpotifyService.LIMIT, 10);

        final ListManager listManager = ListManager.getInstance();

        spotifyService.getNewReleases(options, new SpotifyCallback<NewReleases>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("SearchPager", spotifyError.toString());

                if(listener != null)
                    listener.onError(spotifyError);
            }

            @Override
            public void success(NewReleases newReleases, Response response) {
                List<AlbumSimple> albums = newReleases.albums.items;

                for(final AlbumSimple albumSimple : albums){
                    //Log.d("SearchPage", albumSimple.name);
                    //Log.d("SearchPage", albumSimple.id);
                    //Log.d("SearchPage", albumSimple.uri);
                    //Log.d("SearchPage", albumSimple.href);
                    //Log.d("SearchPage", albumSimple.images.get(1).url);

                    spotifyService.getAlbum(albumSimple.id, new SpotifyCallback<Album>() {
                        @Override
                        public void failure(SpotifyError spotifyError) {
                            Log.d("SearchPage Followup", spotifyError.toString());

                            if(listener != null)
                                listener.onError(spotifyError);
                        }

                        @Override
                        public void success(Album album, Response response) {

                            Log.d("SearchPage Followup", albumSimple.name);
                            Log.d("SearchPage Followup", albumSimple.id);
                            Log.d("SearchPage Followup", albumSimple.images.get(1).url);

                            List<ArtistSimple> list = album.artists;
                            List<String> artists = new ArrayList<>();

                            for(ArtistSimple simple : list){
                                Log.d("SearchPage Followup", simple.name);
                                artists.add(simple.name);
                            }

                            AlbumNew albumNew = new AlbumNew(albumSimple.name, albumSimple.images.get(1).url, artists);

                            listManager.addNewAlbum(albumNew);

                        }

                    });
                }

                if(listener != null)
                    listener.onComplete();
            }
        });
    }

    public void getMyPlayList(){
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 30);

        spotifyService.getMyPlaylists(options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("SearchPager", spotifyError.toString());
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                List<PlaylistSimple> simples = playlistSimplePager.items;

                for(PlaylistSimple simple : simples){
                    Log.d("SearchPager", simple.name);
                    Log.d("SearchPager", simple.images.get(1).url);
                }

            }
        });
    }

    public void getFeatured(){

        spotifyService.getFeaturedPlaylists(new SpotifyCallback<FeaturedPlaylists>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("SearchPager", spotifyError.toString());
            }

            @Override
            public void success(FeaturedPlaylists featuredPlaylists, Response response) {
                List<PlaylistSimple> mlist = featuredPlaylists.playlists.items;

                for(PlaylistSimple simple : mlist)
                {
                    Log.d("SearchPager Simple", simple.name);
                    Log.d("SearchPager Simple", simple.images.get(0).url);

                    ListManager.getInstance().addSimpleList(new SimplePlaylist(simple.name, simple.images.get(0).url));
                }
            }
        });
    }

}
