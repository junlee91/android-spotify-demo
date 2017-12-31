package com.sodastudio.jun.spotify_demo.ui;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.sodastudio.jun.spotify_demo.R;

/**
 * Created by jun on 12/28/17.
 */

public class MainFragment extends Fragment {

    private static final String TAG = "Spotify MainFragment";

    private FragmentManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = getFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button homeLayout = (Button)view.findViewById(R.id.nav_home);
        Button browseLayout = (Button)view.findViewById(R.id.nav_browse);
        Button searchLayout = (Button)view.findViewById(R.id.nav_search);
        Button radioLayout = (Button)view.findViewById(R.id.nav_radio);
        Button libraryLayout = (Button)view.findViewById(R.id.nav_library);


        homeLayout.setOnClickListener(mListener);
        browseLayout.setOnClickListener(mListener);
        searchLayout.setOnClickListener(mListener);
        radioLayout.setOnClickListener(mListener);
        libraryLayout.setOnClickListener(mListener);


        return view;
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.nav_home:
                    Log.d(TAG, "HOME");
                    manager.beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
                    break;
                case R.id.nav_browse:
                    Log.d(TAG, "BROWSE");
                    manager.beginTransaction().replace(R.id.fragment, new BrowseFragment()).commit();
                    break;
                case R.id.nav_search:
                    Log.d(TAG, "SEARCH");
                    //manager.beginTransaction().replace(R.id.fragment, new SearchFragment()).commit();
                    SearchFragment.getFragmentInstance(manager, "SearchFragment");
                    break;
                case R.id.nav_radio:
                    Log.d(TAG, "RADIO");
                    manager.beginTransaction().replace(R.id.fragment, new RadioFragment()).commit();
                    break;
                case R.id.nav_library:
                    Log.d(TAG, "LIBRARY");
                    manager.beginTransaction().replace(R.id.fragment, new LibraryFragment()).commit();
                    break;
            }
        }
    };
}
