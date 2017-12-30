package com.sodastudio.jun.spotify_demo.model;

import android.net.Uri;

import java.net.URL;

/**
 * Created by jun on 12/29/17.
 */

public class Music {

    private String uri;
    private String title;
    private String album;
    private String album_image;
    private long duration;
    private String artist;

    public Music(String u, String t, String a, String a_img, long dura, String art){
        uri = u;
        title = t;
        album = a;
        album_image = a_img;
        duration = dura;
        artist = art;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbum_image() {
        return album_image;
    }

    public long getDuration() {
        return duration;
    }

    public String getArtist() {
        return artist;
    }
}
