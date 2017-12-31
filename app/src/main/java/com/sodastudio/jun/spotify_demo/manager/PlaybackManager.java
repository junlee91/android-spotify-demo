package com.sodastudio.jun.spotify_demo.manager;

import com.sodastudio.jun.spotify_demo.model.Music;

/**
 * Created by jun on 12/31/17.
 */

public class PlaybackManager {

    private static PlaybackManager manager;

    private Music music;

    public static PlaybackManager getInstance(){
        if(manager == null){
            manager = new PlaybackManager();
        }

        return manager;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
