package com.sodastudio.jun.spotify_demo.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import com.sodastudio.jun.spotify_demo.model.AlbumNew;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by jun on 12/28/17.
 */

public class BrowseFragment extends Fragment {

    public static final String TAG = "Sptify Browsefragment";

    private RecyclerView newAlbumsRecyclerView;
    private newAlbumAdapter mAdapter;

    private ListManager listManager;
    private SearchPager.onCompleteListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listManager = ListManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        newAlbumsRecyclerView = view.findViewById(R.id.new_albums_RecyclerView);
        newAlbumsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        updateView();

        return view;
    }

    private void updateView(){

        List<AlbumNew> albumNewList = listManager.getAlbumNewArrayList();

        if(albumNewList.size() == 0){

            listener = new SearchPager.onCompleteListener() {
                @Override
                public void onComplete() {
                    Log.d(TAG, "onComplete!");
                    mAdapter.notifyDataSetChanged();
                    updateView();
                }

                @Override
                public void onError(Throwable error) {

                }
            };

            SearchPager.getInstance(getContext()).getNewRelease(listener);
        }

        if(mAdapter == null)
            mAdapter = new newAlbumAdapter(albumNewList);

        newAlbumsRecyclerView.setAdapter(mAdapter);

    }

    private class newAlbumHolder extends RecyclerView.ViewHolder{

        private ImageView album_image;
        private TextView album_title;
        private TextView album_artists;

        private newAlbumHolder(View itemView) {
            super(itemView);

            album_image = itemView.findViewById(R.id.new_album_image);
            album_title = itemView.findViewById(R.id.new_album_title);
            album_artists = itemView.findViewById(R.id.new_artist_name);
        }

        private void bindAlbum(AlbumNew albumNew){
            final String title = albumNew.getTitle();
            String img_url = albumNew.getImg_url();

            StringBuilder sb = new StringBuilder();

            List<String> artists = albumNew.getArtists();
            for(String s : artists){
                sb.append(s + ", ");
            }

            String artist = sb.toString();

            int index = artist.lastIndexOf(",");
            artist = artist.substring(0, index);

            if(artist.length() > 30){
                artist = artist.substring(0, 30);
                artist += "...";
            }

            album_title.setText(title);
            album_artists.setText(artist);

            Picasso.with(getContext())
                    .load(img_url)
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            final Bitmap copy = source.copy(source.getConfig(), true);
                            source.recycle();

                            return copy;
                        }

                        @Override
                        public String key() {
                            return title;
                        }
                    })
                    .into(album_image);
        }
    }

    private class newAlbumAdapter extends RecyclerView.Adapter<newAlbumHolder>{

        private List<AlbumNew> albumNewsList;

        private newAlbumAdapter(List<AlbumNew> list) {
            albumNewsList = list;
        }

        @Override
        public newAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View view = inflater.inflate(R.layout.new_album_view, parent, false);

            return new newAlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(newAlbumHolder holder, int position) {
            holder.bindAlbum(albumNewsList.get(position));
        }

        @Override
        public int getItemCount() {
            return albumNewsList.size();
        }
    }
}
