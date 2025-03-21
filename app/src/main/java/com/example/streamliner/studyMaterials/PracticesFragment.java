package com.example.streamliner.studyMaterials;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PracticesFragment extends Fragment {
    private static final String TAG = "PracticeFragment";
    private RecyclerView practicesRecyclerView;
    private PracticeAdapter adapter;
    private List<Practice> practiceList;
    private DatabaseReference databaseRef;
    private ProgressBar loadingProgressBar;
    private String courseId;

    public PracticesFragment() {
        // Required empty public constructor
    }

    public static PracticesFragment newInstance(String courseId) {
        PracticesFragment fragment = new PracticesFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practices, container, false);

        // Get courseId from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
        }

        // Initialize views
        practicesRecyclerView = view.findViewById(R.id.practicesRecyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        practiceList = new ArrayList<>();
        adapter = new PracticeAdapter(practiceList, new PracticeAdapter.OnPracticeClickListener() {
            @Override
            public void onPracticeClick(Practice practice, int position) {
                Bundle args = new Bundle();
                args.putString("courseId", courseId);
                args.putInt("practiceId", practiceList.indexOf(practice));
                args.putString("practiceTitle", practice.getTitle());

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_studyMaterialsFragment_to_practiceFragment, args);

                /*PracticeFragment fragment = PracticeFragment.newInstance(courseId, practiceList.indexOf(practice), practice.getTitle());

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer1, fragment)
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        // Setup RecyclerView
        practicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        practicesRecyclerView.setAdapter(adapter);

        // Load practices
        loadPractices();

        return view;
    }

    private void loadPractices() {
        if (courseId == null) return;

        loadingProgressBar.setVisibility(View.VISIBLE);
        DatabaseReference practicesRef = FirebaseDatabase.getInstance().getReference()
                .child("Courses")
                .child(courseId)
                .child("practices");

        practicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                practiceList.clear();
                Log.d(TAG, "Practices data received. Count: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Practice practice = snapshot.getValue(Practice.class);
                    if (practice != null) {
                        practiceList.add(practice);
                        Log.d(TAG, "Loaded practice: " + practice.getTitle());
                    }
                }
                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading practices: " + error.getMessage());
                Toast.makeText(getContext(),
                        "Error loading practices",
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}