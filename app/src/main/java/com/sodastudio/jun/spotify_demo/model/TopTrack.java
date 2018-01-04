package com.sodastudio.jun.spotify_demo.model;

/**
 * Created by jun on 1/3/18.
 */

public class TopTrack {
    private String name;
    private String img_url;

    public TopTrack(String name, String img_url) {
        this.name = name;
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public String getImg_url() {
        return img_url;
    }
}
