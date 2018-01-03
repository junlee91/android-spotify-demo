package com.sodastudio.jun.spotify_demo.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sodastudio.jun.spotify_demo.MainActivity;
import com.sodastudio.jun.spotify_demo.R;
import com.sodastudio.jun.spotify_demo.manager.ListManager;
import com.sodastudio.jun.spotify_demo.manager.SearchPager;
import com.sodastudio.jun.spotify_demo.model.TopArtist;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jun on 12/28/17.
 */

public class HomeFragment extends Fragment{

    public static final String TAG = "Spotify HomeFragment";

    private RecyclerView mMadeForYouRecyclerView;
    private RecyclerView mTopArtistRecyclerView;

    private HorizontalItemAdapter itemAdapter;
    private HorizontalArtistAdapter artistAdapter;

    private SearchPager.onCompleteTopArtistListener listener;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mMadeForYouRecyclerView = view.findViewById(R.id.made_for_you_RecyclerView);
        mTopArtistRecyclerView = view.findViewById(R.id.top_artist_RecyclerView);

        LinearLayoutManager horizontal_home_layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontal_artist_layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mMadeForYouRecyclerView.setLayoutManager(horizontal_home_layout);
        mTopArtistRecyclerView.setLayoutManager(horizontal_artist_layout);

        updateUI();

        return view;
    }

    private void updateUI(){

        setMadeForYou();

        setTopArtists();
    }

    private void setMadeForYou(){
        ArrayList<String> mlist = new ArrayList<>();

        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");

        if(itemAdapter == null)
            itemAdapter = new HorizontalItemAdapter(mlist);
        mMadeForYouRecyclerView.setAdapter(itemAdapter);
    }

    private void setTopArtists(){
        List<TopArtist> artists = ListManager.getInstance().getTopArtists();

        if(artists.size() == 0){

            listener = new SearchPager.onCompleteTopArtistListener() {
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

            SearchPager.getInstance(getContext()).getMyTopArtist(listener);
        }

        if(artistAdapter == null)
            artistAdapter = new HorizontalArtistAdapter(artists);

        mTopArtistRecyclerView.setAdapter(artistAdapter);
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

    private class HorizontalItemHolder extends RecyclerView.ViewHolder{

        private HorizontalItemHolder(View itemView) {
            super(itemView);
        }

        private void bindItem(String test){

        }
    }

    private class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemHolder>{

        private List<String> testList;

        private HorizontalItemAdapter(List<String> list){
            testList = list;
        }

        @Override
        public HorizontalItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.album_view, parent, false);

            return new HorizontalItemHolder(view);
        }

        @Override
        public void onBindViewHolder(HorizontalItemHolder holder, int position) {

            holder.bindItem(testList.get(position));
        }

        @Override
        public int getItemCount() {
            return testList.size();
        }
    }


}
