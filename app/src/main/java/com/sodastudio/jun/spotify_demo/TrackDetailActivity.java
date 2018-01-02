package com.sodastudio.jun.spotify_demo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sodastudio.jun.spotify_demo.model.Music;
import com.sodastudio.jun.spotify_demo.ui.SearchResultFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class TrackDetailActivity extends AppCompatActivity {

    private static final String TAG = "Spotify TrackDetail";

    private Music detailMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        detailMusic = getIntent().getParcelableExtra(SearchResultFragment.DETAIL_MUSIC);

        final ImageView albumImage = findViewById(R.id.detail_album_image_field);
        final TextView titleView = findViewById(R.id.detail_track_title_field);
        final TextView artistView = findViewById(R.id.detail_artist_field);
        final TextView albumView = findViewById(R.id.detail_album_field);

        final TextView cancelView =findViewById(R.id.detail_cancel_button);
        cancelView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TrackDetailActivity.this.finish();
            }
        });

        Picasso.with(this)
                .load(detailMusic.getAlbum_image())
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        // really ugly darkening trick
                        final Bitmap copy = source.copy(source.getConfig(), true);
                        source.recycle();
                        return copy;
                    }

                    @Override
                    public String key() {
                        return "darken";
                    }
                })
                .into(albumImage);

        String title = detailMusic.getTitle();
        String album = detailMusic.getAlbum();
        String artist = detailMusic.getArtist();

        if(title.length() > 40){
            title = title.substring(0, 40);
            title += "...";
        }

        if(album.length() > 40){
            album = album.substring(0,40);
            album += "...";
        }

        if(artist.length() > 40){
            artist = artist.substring(0,40);
            artist += "...";
        }

        titleView.setText(title);
        artistView.setText(artist);
        albumView.setText(album);

        Log.d(TAG, detailMusic.getTitle());
    }
}
