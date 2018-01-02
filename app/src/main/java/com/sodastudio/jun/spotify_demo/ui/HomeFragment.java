package com.sodastudio.jun.spotify_demo.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sodastudio.jun.spotify_demo.MainActivity;
import com.sodastudio.jun.spotify_demo.R;

import java.util.ArrayList;

/**
 * Created by jun on 12/28/17.
 */

public class HomeFragment extends Fragment{

    private RecyclerView mMadeForYouRecyclerView;
    private HorizontalItemAdapter itemAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mMadeForYouRecyclerView = view.findViewById(R.id.made_for_you_RecyclerView);
        LinearLayoutManager horizontal_layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mMadeForYouRecyclerView.setLayoutManager(horizontal_layout);

        updateUI();

        return view;
    }

    private void updateUI(){

        ArrayList<String> mlist = new ArrayList<>();

        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");
        mlist.add("Test");

        itemAdapter = new HorizontalItemAdapter(mlist);

        mMadeForYouRecyclerView.setAdapter(itemAdapter);
    }

    private class HorizontalItemHolder extends RecyclerView.ViewHolder{

        private HorizontalItemHolder(View itemView) {
            super(itemView);
        }

        private void bindItem(String test){

        }
    }

    private class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemHolder>{

        private ArrayList<String> testList;

        private HorizontalItemAdapter(ArrayList<String> list){
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
