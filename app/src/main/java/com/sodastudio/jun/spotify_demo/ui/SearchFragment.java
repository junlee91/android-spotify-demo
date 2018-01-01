package com.sodastudio.jun.spotify_demo.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sodastudio.jun.spotify_demo.R;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;

/**
 * Created by jun on 12/28/17.
 */

public class SearchFragment extends Fragment{

    private static final String TAG = "Spotify SearchFragment";

    private TextView textViewSearch;
    private EditText editTextSearch;
    private ScalingLayout scalingLayout;

    @SuppressLint("ResourceType")
    public static SearchFragment getFragmentInstance(FragmentManager fm, String tag){
        SearchFragment fragment = (SearchFragment)fm.findFragmentByTag(tag);

        if(fragment == null){
            fragment = new SearchFragment();

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment, fragment, tag).commitAllowingStateLoss();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        textViewSearch = view.findViewById(R.id.textViewSearch);
        final RelativeLayout searchLayout = view.findViewById(R.id.searchLayout);
        final ImageButton searchButton = view.findViewById(R.id.search_text_button);

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

                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();

                        ft.add(R.id.fragment, SearchResultFragment.newInstance(query))
                                .addToBackStack(TAG)
                                .commit();



                        textViewSearch.setText(query);
                    }

                    break;
            }
        }
    };
}































