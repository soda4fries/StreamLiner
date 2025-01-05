package com.example.streamliner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamliner.courseDiscovery.SearchFragment;
import com.example.streamliner.courseDiscovery.TopCoursesFragment;

public class BlankFragment extends Fragment {

    public BlankFragment() {
        // Required empty public constructor
    }

    /*public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        SearchFragment fragment = SearchFragment.newInstance(0);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer1, fragment)
                .addToBackStack(null)
                .commit();

        return view;
    }
}