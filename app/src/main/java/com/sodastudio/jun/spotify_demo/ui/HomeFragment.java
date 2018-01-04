package com.sodastudio.jun.spotify_demo.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sodastudio.jun.spotify_demo.R;
import com.sodastudio.jun.spotify_demo.manager.ListManager;
import com.sodastudio.jun.spotify_demo.manager.SearchPager;
import com.sodastudio.jun.spotify_demo.model.SimplePlaylist;
import com.sodastudio.jun.spotify_demo.model.TopArtist;
import com.sodastudio.jun.spotify_demo.model.TopTrack;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by jun on 12/28/17.
 */

public class HomeFragment extends Fragment{

    public static final String TAG = "Spotify HomeFragment";

    private RecyclerView mMadeForYouRecyclerView;
    private RecyclerView mTopArtistRecyclerView;
    private RecyclerView mTopTrackRecyclerView;

    private HorizontalSimpleListAdapter simpleListAdapter;
    private HorizontalArtistAdapter artistAdapter;
    private HorizontalTrackAdapter trackAdapter;

    private SearchPager.onCompleteTopArtistListener artistListener;
    private SearchPager.onCompleteTopTrackListener trackListener;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mMadeForYouRecyclerView = view.findViewById(R.id.made_for_you_RecyclerView);
        mTopArtistRecyclerView = view.findViewById(R.id.top_artist_RecyclerView);
        mTopTrackRecyclerView = view.findViewById(R.id.top_tracks_RecyclerView);

        LinearLayoutManager horizontal_home_layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontal_artist_layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontal_track_layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mMadeForYouRecyclerView.setLayoutManager(horizontal_home_layout);
        mTopArtistRecyclerView.setLayoutManager(horizontal_artist_layout);
        mTopTrackRecyclerView.setLayoutManager(horizontal_track_layout);

        updateUI();

        return view;
    }

    private void updateUI(){

        setMadeForYou();

        setTopArtists();

        setTopTracks();
    }

    private void setMadeForYou(){

        ArrayList<SimplePlaylist> mlist = ListManager.getInstance().getSimplePlaylists();

        if(simpleListAdapter == null)
            simpleListAdapter = new HorizontalSimpleListAdapter(mlist.subList(0,10));

        mMadeForYouRecyclerView.setAdapter(simpleListAdapter);
    }

    private void setTopArtists(){
        List<TopArtist> artists = ListManager.getInstance().getTopArtists();

        if(artists.size() == 0){

            artistListener = new SearchPager.onCompleteTopArtistListener() {
                @Override
                public void onComplete() {
                    Log.d(TAG, "onComplete!");
                    artistAdapter.notifyDataSetChanged();
                    setTopArtists();
                }

                @Override
                public void onError(Throwable error) {

                }
            };

            SearchPager.getInstance(getContext()).getMyTopArtist(artistListener);
        }

        if(artistAdapter == null)
            artistAdapter = new HorizontalArtistAdapter(artists);

        mTopArtistRecyclerView.setAdapter(artistAdapter);
    }

    private void setTopTracks(){
        List<TopTrack> tracks = ListManager.getInstance().getTopTracks();

        if(tracks.size() == 0){
            trackListener = new SearchPager.onCompleteTopTrackListener() {
                @Override
                public void onComplete() {
                    trackAdapter.notifyDataSetChanged();
                    setTopTracks();
                }

                @Override
                public void onError(Throwable error) {

                }

            };

            SearchPager.getInstance(getContext()).getMyTopTracks(trackListener);
        }

        if(trackAdapter == null)
            trackAdapter = new HorizontalTrackAdapter(tracks);

        mTopTrackRecyclerView.setAdapter(trackAdapter);

    }

    private class HorizontalTrackHolder extends RecyclerView.ViewHolder{

        private ImageView album_image_field;
        private TextView album_name_view;

        private HorizontalTrackHolder(View itemView) {
            super(itemView);

            album_image_field = itemView.findViewById(R.id.track_image_field);
            album_name_view = itemView.findViewById(R.id.track_name);
        }

        private void bindTrack(final TopTrack track){

            album_name_view.setText(track.getName());

            Picasso.with(getContext())
                    .load(track.getImg_url())
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            final Bitmap copy = source.copy(source.getConfig(), true);
                            source.recycle();

                            return copy;
                        }

                        @Override
                        public String key() {
                            return track.getName();
                        }
                    })
                    .into(album_image_field);

        }

    }

    private class HorizontalTrackAdapter extends RecyclerView.Adapter<HorizontalTrackHolder>{

        private List<TopTrack> tracks;

        private HorizontalTrackAdapter(List<TopTrack> list){
            tracks = list;
        }

        @Override
        public HorizontalTrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.track_view, parent, false);

            return new HorizontalTrackHolder(view);
        }

        @Override
        public void onBindViewHolder(HorizontalTrackHolder holder, int position) {
            holder.bindTrack(tracks.get(position));
        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }
    }


    private class HorizontalArtistHolder extends RecyclerView.ViewHolder{

        private TopArtist artist;
        private TextView artist_name_view;
        private ImageView artist_image_field;

        private HorizontalArtistHolder(View itemView) {
            super(itemView);

            artist_name_view = itemView.findViewById(R.id.artist_name);
            artist_image_field = itemView.findViewById(R.id.artist_image_field);
        }

        private void bindArtist(final TopArtist artist){
            this.artist = artist;

            artist_name_view.setText(artist.getName());

            Picasso.with(getContext())
                    .load(artist.getImg_url())
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            final Bitmap copy = source.copy(source.getConfig(), true);
                            source.recycle();

                            return copy;
                        }

                        @Override
                        public String key() {
                            return artist.getName();
                        }
                    })
                    .into(artist_image_field);


        }
    }

    private class HorizontalArtistAdapter extends RecyclerView.Adapter<HorizontalArtistHolder>{

        private List<TopArtist> artists;

        private HorizontalArtistAdapter(List<TopArtist> list){
            this.artists = list;
        }

        @Override
        public HorizontalArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.artist_view, parent, false);

            return new HorizontalArtistHolder(view);
        }

        @Override
        public void onBindViewHolder(HorizontalArtistHolder holder, int position) {
            holder.bindArtist(artists.get(position));
        }

        @Override
        public int getItemCount() {
            return artists.size();
        }
    }



    private class HorizontalSimpleListHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView titleView;

        private HorizontalSimpleListHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.playlist_album_image);
            titleView = itemView.findViewById(R.id.playlist_album_title);
        }

        private void bindItem(final SimplePlaylist simple){
            titleView.setText(simple.getName());

            Picasso.with(getContext())
                    .load(simple.getImg_url())
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            final Bitmap copy = source.copy(source.getConfig(), true);
                            source.recycle();

                            return copy;
                        }

                        @Override
                        public String key() {
                            return simple.getName();
                        }
                    })
                    .into(imageView);
        }
    }

    private class HorizontalSimpleListAdapter extends RecyclerView.Adapter<HorizontalSimpleListHolder>{

        private List<SimplePlaylist> testList;

        private HorizontalSimpleListAdapter(List<SimplePlaylist> list){
            testList = list;
        }

        @Override
        public HorizontalSimpleListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.album_view, parent, false);

            return new HorizontalSimpleListHolder(view);
        }

        @Override
        public void onBindViewHolder(HorizontalSimpleListHolder holder, int position) {

            holder.bindItem(testList.get(position));
        }

        @Override
        public int getItemCount() {
            return testList.size();
        }
    }


}
