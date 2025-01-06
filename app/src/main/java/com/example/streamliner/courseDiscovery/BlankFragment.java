package com.example.streamliner.courseDiscovery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamliner.R;


public class BlankFragment extends Fragment {

    public BlankFragment() {

    }


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