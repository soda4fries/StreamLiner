package com.example.streamliner.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentDashboardBinding;
import com.example.streamliner.home.Adapter.LeaderboardAdapter;
import com.example.streamliner.home.Item.FeatureItem;
import com.example.streamliner.home.Adapter.FeaturesAdapter;
import com.example.streamliner.home.Item.LeaderboardItem;
import com.example.streamliner.home.Adapter.NewsAdapter;
import com.example.streamliner.home.Item.NewsItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private LeaderboardAdapter leaderboardAdapter;
    private NewsAdapter newsAdapter;
    private FeaturesAdapter featuresAdapter;
    private FirebaseFirestore db;
    private DatabaseReference realtimeDb;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeFirebase();
        setupRecyclerViews();
        loadData();

        binding.viewLeaderboardButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_dashboardFragment_to_Quiz_listFragments);
        });
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        realtimeDb = FirebaseDatabase.getInstance().getReference();
    }

    private void setupRecyclerViews() {
        // Leaderboard RecyclerView
        leaderboardAdapter = new LeaderboardAdapter();
        binding.leaderboardRecyclerView.setAdapter(leaderboardAdapter);
        binding.leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // News RecyclerView
        newsAdapter = new NewsAdapter();
        binding.newsRecyclerView.setAdapter(newsAdapter);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Features RecyclerView
        featuresAdapter = new FeaturesAdapter();
        binding.featuresRecyclerView.setAdapter(featuresAdapter);
        binding.featuresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadData() {
        // Load Leaderboard Data
        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("DashboardFragment", "Error loading leaderboard", error);
                        return;
                    }

                    List<LeaderboardItem> items = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            LeaderboardItem item = doc.toObject(LeaderboardItem.class);
                            items.add(item);
                        }
                        leaderboardAdapter.submitList(items);
                    }
                });

        // Load News Data
        realtimeDb.child("news")
                .limitToLast(5)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<NewsItem> items = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            NewsItem item = child.getValue(NewsItem.class);
                            if (item != null) {
                                items.add(item);
                            }
                        }
                        newsAdapter.submitList(items);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DashboardFragment", "Error loading news" + error);
                    }
                });

        // Load Features Data
        db.collection("features")
                .whereEqualTo("status", "upcoming")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("DashboardFragment", "Error loading features", error);
                        return;
                    }

                    List<FeatureItem> items = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            FeatureItem item = doc.toObject(FeatureItem.class);
                            items.add(item);
                        }
                        featuresAdapter.submitList(items);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

