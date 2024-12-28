package com.example.streamliner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideosFragment extends Fragment {
    private RecyclerView videosRecyclerView;
    private VideoAdapter adapter;
    private List<Video> videoList;
    private DatabaseReference databaseRef;
    private ProgressBar loadingProgressBar;
    private String courseId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    public VideosFragment() {
        // Required empty public constructor
    }

    public static VideosFragment newInstance(String courseId) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        // Get courseId from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
        }

        // Initialize views
        videosRecyclerView = view.findViewById(R.id.videosRecyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        // Initialize video list and adapter
        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList, video -> {
            // Handle video click
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(video.getLink()));
            startActivity(intent);
        });

        // Setup RecyclerView
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        videosRecyclerView.setAdapter(adapter);

        // Load videos
        loadVideos();

        return view;
    }

    private void loadVideos() {
        if (courseId == null) return;

        loadingProgressBar.setVisibility(View.VISIBLE);
        databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("Courses")
                .child(courseId)
                .child("videos");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoList.clear();
                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    Video video = videoSnapshot.getValue(Video.class);
                    if (video != null) {
                        videoList.add(video);
                    }
                }
                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),
                        "Error loading videos: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}