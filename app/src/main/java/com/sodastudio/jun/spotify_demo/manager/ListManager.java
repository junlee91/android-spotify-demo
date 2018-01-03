package com.sodastudio.jun.spotify_demo.manager;

import com.sodastudio.jun.spotify_demo.model.AlbumNew;
import com.sodastudio.jun.spotify_demo.model.ArtistSearch;
import com.sodastudio.jun.spotify_demo.model.Music;

import java.util.ArrayList;

/**
 * Created by jun on 12/30/17.
 */

public class ListManager {

    private static ListManager listManager;

    private ArrayList<Music> trackLists;
    private ArrayList<ArtistSearch> artistSearches;
    private ArrayList<AlbumNew> albumNewArrayList;

    public static ListManager getInstance(){
        if(listManager == null){
            listManager = new ListManager();
        }

        return listManager;
    }

    private ListManager(){
        trackLists = new ArrayList<>();
        artistSearches = new ArrayList<>();
        albumNewArrayList = new ArrayList<>();
    }

    public void addNewAlbum(AlbumNew albumNew){
        albumNewArrayList.add(albumNew);
    }

    public ArrayList<AlbumNew> getAlbumNewArrayList(){
        return albumNewArrayList;
    }

    public ArrayList<ArtistSearch> getArtists() {
        return artistSearches;
    }

    public void addArtist(ArtistSearch search){

        ArtistSearch found = null;

        for(ArtistSearch artistSearch : artistSearches)
        {
            if(artistSearch.getName().equals(search.getName()))
            {
                found = artistSearch;
            }
        }

        if(found != null)
            artistSearches.remove(found);

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
