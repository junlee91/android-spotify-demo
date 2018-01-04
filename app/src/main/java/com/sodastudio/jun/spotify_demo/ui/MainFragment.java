package com.sodastudio.jun.spotify_demo.ui;


import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.sodastudio.jun.spotify_demo.R;

/**
 * Created by jun on 12/28/17.
 */

public class MainFragment extends Fragment {

    private static final String TAG = "Spotify MainFragment";

    private FragmentManager manager;

    private TextView homeText;
    private TextView browseText;
    private TextView searchText;
    private TextView radioText;
    private TextView libraryText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = getFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button homeLayout = view.findViewById(R.id.nav_home);
        Button browseLayout = view.findViewById(R.id.nav_browse);
        Button searchLayout = view.findViewById(R.id.nav_search);
        Button radioLayout = view.findViewById(R.id.nav_radio);
        Button libraryLayout = view.findViewById(R.id.nav_library);

        homeText = view.findViewById(R.id.nav_home_text);
        browseText = view.findViewById(R.id.nav_browse_text);
        searchText = view.findViewById(R.id.nav_search_text);
        radioText = view.findViewById(R.id.nav_radio_text);
        libraryText = view.findViewById(R.id.nav_library_text);

        homeLayout.setOnClickListener(mListener);
        browseLayout.setOnClickListener(mListener);
        searchLayout.setOnClickListener(mListener);
        radioLayout.setOnClickListener(mListener);
        libraryLayout.setOnClickListener(mListener);

        //homeLayout.callOnClick();

        return view;
    }

    Drawable home;
    Drawable browse;
    Drawable search;
    Drawable radio;
    Drawable library;
    int focusMode;
    int defocusMode;

    int prev_clicked_id = -1;
    View prev_view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        home = getResources().getDrawable(R.drawable.ic_home_black_24dp, null);
        browse = getResources().getDrawable(R.drawable.ic_open_in_browser_black_24dp, null);
        search = getResources().getDrawable(R.drawable.ic_search_black_24dp, null);
        radio = getResources().getDrawable(R.drawable.ic_radio_black_24dp, null);
        library = getResources().getDrawable(R.drawable.ic_library_music_black_24dp, null);

        focusMode = getResources().getColor(R.color.colorWhite, null);;
        defocusMode = getResources().getColor(R.color.colorNavIcon, null);
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.nav_click));

            switch (view.getId())
            {
                case R.id.nav_home:
                    Log.d(TAG, "HOME");

                    if(view.isActivated()) break;

                    manager.beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();

                    setFocus(R.id.nav_home, view);
                    setDeFocus(prev_clicked_id, prev_view);

                    prev_clicked_id = R.id.nav_home;
                    prev_view = view;
                    break;
                case R.id.nav_browse:
                    Log.d(TAG, "BROWSE");

                    if(view.isActivated()) break;

                    manager.beginTransaction().replace(R.id.fragment, new BrowseFragment()).commit();

                    setFocus(R.id.nav_browse, view);
                    setDeFocus(prev_clicked_id, prev_view);

                    prev_clicked_id = R.id.nav_browse;
                    prev_view = view;
                    break;
                case R.id.nav_search:
                    Log.d(TAG, "SEARCH");

                    if(view.isActivated()) break;

                    SearchFragment.getFragmentInstance(manager, "SearchFragment");

                    setFocus(R.id.nav_search, view);
                    setDeFocus(prev_clicked_id, prev_view);

                    prev_clicked_id = R.id.nav_search;
                    prev_view = view;
                    break;
                case R.id.nav_radio:
                    Log.d(TAG, "RADIO");

                    if(view.isActivated()) break;

                    manager.beginTransaction().replace(R.id.fragment, new RadioFragment()).commit();

                    setFocus(R.id.nav_radio, view);
                    setDeFocus(prev_clicked_id, prev_view);

                    prev_clicked_id = R.id.nav_radio;
                    prev_view = view;
                    break;
                case R.id.nav_library:
                    Log.d(TAG, "LIBRARY");

                    if(view.isActivated()) break;

                    manager.beginTransaction().replace(R.id.fragment, new LibraryFragment()).commit();

                    setFocus(R.id.nav_library, view);
                    setDeFocus(prev_clicked_id, prev_view);

                    prev_clicked_id = R.id.nav_library;
                    prev_view = view;
                    break;
            }
        }
    };

    private void setFocus(int res_id, View view){
        switch (res_id)
        {
            case R.id.nav_home:
                home.setTint(focusMode);
                view.setBackground(home);
                homeText.setTextColor(focusMode);
                homeText.setTypeface(Typeface.DEFAULT_BOLD);
                view.setActivated(true);
                break;
            case R.id.nav_browse:
                browse.setTint(focusMode);
                view.setBackground(browse);
                browseText.setTextColor(focusMode);
                browseText.setTypeface(Typeface.DEFAULT_BOLD);
                view.setActivated(true);
                break;
            case R.id.nav_search:
                search.setTint(focusMode);
                view.setBackground(search);
                searchText.setTextColor(focusMode);
                searchText.setTypeface(Typeface.DEFAULT_BOLD);
                view.setActivated(true);
                break;
            case R.id.nav_radio:
                radio.setTint(focusMode);
                view.setBackground(radio);
                radioText.setTextColor(focusMode);
                radioText.setTypeface(Typeface.DEFAULT_BOLD);
                view.setActivated(true);
                break;
            case R.id.nav_library:
                library.setTint(focusMode);
                view.setBackground(library);
                libraryText.setTextColor(focusMode);
                libraryText.setTypeface(Typeface.DEFAULT_BOLD);
                view.setActivated(true);
                break;
        }
    }
    private void setDeFocus(int res_id, View view){
        switch (res_id)
        {
            case R.id.nav_home:
                home.setTint(defocusMode);
                view.setBackground(home);
                homeText.setTextColor(defocusMode);
                homeText.setTypeface(Typeface.DEFAULT);
                view.setActivated(false);
                break;
            case R.id.nav_browse:
                browse.setTint(defocusMode);
                view.setBackground(browse);
                browseText.setTextColor(defocusMode);
                browseText.setTypeface(Typeface.DEFAULT);
                view.setActivated(false);
                break;
            case R.id.nav_search:
                search.setTint(defocusMode);
                view.setBackground(search);
                searchText.setTextColor(defocusMode);
                searchText.setTypeface(Typeface.DEFAULT);
                view.setActivated(false);
                break;
            case R.id.nav_radio:
                radio.setTint(defocusMode);
                view.setBackground(radio);
                radioText.setTextColor(defocusMode);
                radioText.setTypeface(Typeface.DEFAULT);
                view.setActivated(false);
                break;
            case R.id.nav_library:
                library.setTint(defocusMode);
                view.setBackground(library);
                libraryText.setTextColor(defocusMode);
                libraryText.setTypeface(Typeface.DEFAULT);
                view.setActivated(false);
                break;
            case -1:
                break;
            default:
                break;
        }
    }
}
