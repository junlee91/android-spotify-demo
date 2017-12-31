package com.sodastudio.jun.spotify_demo.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by jun on 12/28/17.
 */

public class SearchFragment extends Fragment{

    private static final String TAG = "Spotify SearchFragment";

    public static final String DETAIL_MUSIC = "Detail Music";

    private TextView textViewSearch;
    private EditText editTextSearch;
    private ScalingLayout scalingLayout;

    private RecyclerView mRecyclerView;
    private TrackListAdapter mAdapter;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchListener;
    private MainActivity.OnPlaybackListener mPlaybackListener;

    private TrackListManager trackListManager;
    private PlaybackManager playbackManager;

    private LinearLayoutManager layoutManager;

    public static SearchFragment getFragmentInstance(FragmentManager fm, String tag){
        SearchFragment fragment = (SearchFragment)fm.findFragmentByTag(tag);

        if(fragment == null){
            fragment = new SearchFragment();
            fm.beginTransaction().replace(R.id.fragment, fragment, tag).commitAllowingStateLoss();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchPager = SearchPager.getInstance(getContext());
        setRetainInstance(true);
        trackListManager = TrackListManager.getInstance();
        playbackManager = PlaybackManager.getInstance();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        textViewSearch = view.findViewById(R.id.textViewSearch);
        final RelativeLayout searchLayout = view.findViewById(R.id.searchLayout);
        final ImageButton searchButton = view.findViewById(R.id.search_text_button);

        mRecyclerView = view.findViewById(R.id.track_list_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        searchButton.setOnClickListener(mListener);
        scalingLayout = view.findViewById(R.id.scalingLayout);

        scalingLayout.setListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                ViewCompat.animate(textViewSearch).alpha(1).setDuration(150).start();
                ViewCompat.animate(searchLayout).alpha(0).setDuration(150).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        textViewSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        searchLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
            }

            @Override
            public void onExpanded() {
                ViewCompat.animate(textViewSearch).alpha(0).setDuration(200).start();
                ViewCompat.animate(searchLayout).alpha(1).setDuration(200).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        searchLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        textViewSearch.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
            }

            @Override
            public void onProgress(float progress) {

            }
        });

        scalingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingLayout.getState() == State.COLLAPSED) {
                    scalingLayout.expand();
                }
            }
        });

        view.findViewById(R.id.rootLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingLayout.getState() == State.EXPANDED) {
                    scalingLayout.collapse();

                    if(editTextSearch.getText().toString().equals(""))
                        textViewSearch.setText("Search");
                }
            }
        });

        updateView();
        return view;
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.search_text_button:
                    String query = editTextSearch.getText().toString();

                    Log.d(TAG, "Query: " + query);

                    scalingLayout.collapse();

                    if(query.equals("")){
                        textViewSearch.setText("Search");
                    } else {
                        textViewSearch.setText(query);

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

                    break;
            }
        }
    };

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
                    playingMusic.setPlaying(true);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void Finish(Music finishMusic) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        playbackManager = PlaybackManager.getInstance();
        state = playbackManager.getState();

        if(state != null){
            layoutManager.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }
}































