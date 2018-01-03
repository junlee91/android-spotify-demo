package com.sodastudio.jun.spotify_demo.manager;

import com.sodastudio.jun.spotify_demo.model.ArtistSearch;
import com.sodastudio.jun.spotify_demo.model.Music;

import java.util.ArrayList;

/**
 * Created by jun on 12/30/17.
 */

public class TrackListManager {

    private static TrackListManager trackListManager;

    private ArrayList<Music> trackLists;
    private ArrayList<ArtistSearch> artistSearches;

    public static TrackListManager getInstance(){
        if(trackListManager == null){
            trackListManager = new TrackListManager();
        }

        return trackListManager;
    }

    private TrackListManager(){
        trackLists = new ArrayList<>();
        artistSearches = new ArrayList<>();
    }

    public ArrayList<ArtistSearch> getArtists() {
        return artistSearches;
    }

    public void addArtist(ArtistSearch search){

        for(ArtistSearch artistSearch : artistSearches)
        {
            if(artistSearch.getName().equals(search.getName()))
            {
                artistSearches.remove(artistSearch);
            }
        }

        artistSearches.add(0, search);
    }

    public ArrayList<Music> getTrackLists() {
        return trackLists;
    }

    public void addTrack(Music music){
        trackLists.add(music);
    }

    public void clearList(){
        trackLists.clear();
    }

    public Music findCurrentMusic(String title, String album){
        for(Music m : trackLists){
            if(m.getTitle().equals(title) && m.getAlbum().equals(album)){
                return m;
            }
        }

        return null;
    }
}
