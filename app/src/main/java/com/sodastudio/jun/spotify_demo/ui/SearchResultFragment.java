package com.sodastudio.jun.spotify_demo.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sodastudio.jun.spotify_demo.MainActivity;
import com.sodastudio.jun.spotify_demo.R;
import com.sodastudio.jun.spotify_demo.TrackDetailActivity;
import com.sodastudio.jun.spotify_demo.manager.PlaybackManager;
import com.sodastudio.jun.spotify_demo.manager.SearchPager;
import com.sodastudio.jun.spotify_demo.manager.TrackListManager;
import com.sodastudio.jun.spotify_demo.model.Music;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by jun on 12/31/17.
 */

public class SearchResultFragment extends Fragment{

    public static final String QUERY = "QUERY";
    public static final String TAG = "Spotify SearchResult";
    public static final String DETAIL_MUSIC = "Detail Music";

    private String query;

    private RecyclerView mRecyclerView;
    private TrackListAdapter mAdapter;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchListener;
    private MainActivity.OnPlaybackListener mPlaybackListener;

    private TrackListManager trackListManager;
    private PlaybackManager playbackManager;

    private LinearLayoutManager layoutManager;

    public static SearchResultFragment newInstance(String query){
        Bundle args = new Bundle();
        args.putString(QUERY, query);

        SearchResultFragment searchResultFragment = new SearchResultFragment();
        searchResultFragment.setArguments(args);

        return searchResultFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchPager = SearchPager.getInstance(getContext());
        setRetainInstance(true);
        trackListManager = TrackListManager.getInstance();
        playbackManager = PlaybackManager.getInstance();

        playbackManager.setSearchResultFragmentAdded(true);

        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        query = getArguments().getString(QUERY);

        Log.d(TAG, "Query: " + query);

        View view = inflater.inflate(R.layout.fragment_search_result, container, false);


        playbackManager = PlaybackManager.getInstance();
        state = playbackManager.getState();

        layoutManager = new LinearLayoutManager(getActivity());

        if(state != null){
            layoutManager.onRestoreInstanceState(state);
        }

        mRecyclerView = view.findViewById(R.id.track_list_recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(!query.equals("empty"))
            queryData();
        else
            updateView();

        return view;
    }

    private void queryData(){

        mSearchListener = new SearchPager.CompleteListener() {
            @Override
            public void onComplete(List<Track> items) {

                trackListManager.clearList();

                for(Track track : items){
                    //Log.d(TAG, "success! link: " + track.uri);                      // music link
                    //Log.d(TAG, "success! title: " + track.name);                        // title
                    //Log.d(TAG, "success! album: " + track.album.name);                  // album name
                    //Log.d(TAG, "success! album img: " + track.album.images.get(0).url); // album image
                    //Log.d(TAG, "success! duration: " + track.duration_ms);             // song duration
                    //Log.d(TAG, "success! artists: " + track.artists.get(0).name);     // artists

                    Music music = new Music(
                            track.uri,
                            track.name,
                            track.album.name,
                            track.album.images.get(0).url,
                            track.duration_ms,
                            track.artists.get(0).name
                    );

                    trackListManager.addTrack(music);
                }

                updateView();
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, error.getMessage());
            }
        };

        mSearchPager.getTracksFromSearch(query, mSearchListener);
    }

    private void updateView(){

        if(mAdapter == null)
            mAdapter = new TrackListAdapter(trackListManager.getTrackLists());
        mRecyclerView.setAdapter(mAdapter);
    }

    private class TrackListHolder extends RecyclerView.ViewHolder
    {
        private Music music;
        private TextView title_text;
        private TextView artist_text;
        private TextView album_text;
        private ImageButton more_button;

        private SpotifyPlayer mPlayer = MainActivity.mPlayer;

        private TrackListHolder(final View itemView){
            super(itemView);

            title_text = itemView.findViewById(R.id.title_field);
            artist_text = itemView.findViewById(R.id.artist_field);
            album_text = itemView.findViewById(R.id.album_field);
            more_button = itemView.findViewById(R.id.more_horiz);

            more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getContext(), TrackDetailActivity.class);

                    Bundle args = new Bundle();
                    args.putParcelable(DETAIL_MUSIC, music);

                    intent.putExtras(args);
                    startActivity(intent);
                }
            });


            mPlaybackListener = new MainActivity.OnPlaybackListener() {
                @Override
                public void Play(Music playingMusic) {
                    Log.d(TAG, playingMusic.getTitle() + " is playing");
                    playingMusic.setPlaying(true);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void Finish(Music finishMusic) {
                    Log.d(TAG, finishMusic.getTitle() + " finished");
                    finishMusic.setPlaying(false);
                    mAdapter.notifyDataSetChanged();
                }
            };


            title_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(music.isPlaying()) return;

                    playbackManager = PlaybackManager.getInstance();

                    Music prevMusic = playbackManager.getMusic();
                    if(prevMusic != null)
                        prevMusic.setPlaying(false);

                    mPlayer.playUri(null, music.getUri(), 0, 0);

                    playbackManager.setMusic(music);

                    ((MainActivity)getActivity()).setListener(mPlaybackListener, music);
                }
            });
        }

        private void bindMusic(Music m)
        {
            music = m;

            String title = music.getTitle();
            String album = music.getAlbum();

            if(title.length() > 40){
                title = title.substring(0, 40);
                title += "...";
            }

            if(album.length() > 40){
                album = album.substring(0,40);
                album += "...";
            }

            title_text.setText(title);
            artist_text.setText(music.getArtist());
            album_text.setText(album);

            if(music.isPlaying())
                title_text.setTextColor(getResources().getColor(R.color.colorAccent, null));
            else
                title_text.setTextColor(getResources().getColor(R.color.colorWhite, null));


        }
    }

    private class TrackListAdapter extends RecyclerView.Adapter<TrackListHolder>{

        private List<Music> musicList;
        private TrackListAdapter(List<Music> list){
            musicList = list;
        }

        @Override
        public TrackListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            View view = layoutInflater.inflate(R.layout.music, parent, false);

            return new TrackListHolder(view);
        }

        @Override
        public void onBindViewHolder(TrackListHolder holder, int position) {
            Music music = musicList.get(position);

            holder.bindMusic(music);
        }

        @Override
        public int getItemCount() {
            return musicList.size();
        }

    }

    Parcelable state;

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        state = layoutManager.onSaveInstanceState();

        playbackManager = PlaybackManager.getInstance();
        playbackManager.setState(state);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        //playbackManager = PlaybackManager.getInstance();
        //playbackManager.setSearchResultFragmentAdded(false);
    }


}





















