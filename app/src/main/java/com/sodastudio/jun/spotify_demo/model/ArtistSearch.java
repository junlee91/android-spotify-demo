package com.sodastudio.jun.spotify_demo.model;

import android.graphics.Bitmap;

/**
 * Created by jun on 1/2/18.
 */

public class ArtistSearch {

    private String name;
    private Bitmap image;

    public ArtistSearch(String name, Bitmap img) {
        this.name = name;
        this.image = img;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage(){
        return image;
    }
}
