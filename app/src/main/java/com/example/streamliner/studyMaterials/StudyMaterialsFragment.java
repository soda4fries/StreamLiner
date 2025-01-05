package com.example.streamliner.studyMaterials;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StudyMaterialsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView courseNameTV, courseDescriptionTV;
    private String courseId;
    private String courseName;
    private String courseDescription;

    public StudyMaterialsFragment() {
        // Required empty public constructor
    }

    /*public static StudyMaterialsFragment newInstance(String courseId, String courseName, String courseDescription) {
        StudyMaterialsFragment fragment = new StudyMaterialsFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putString("courseName", courseName);
        args.putString("courseDescription", courseDescription);
        //args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_materials, container, false);

        // Get course details from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
            courseName = getArguments().getString("courseName");
            courseDescription = getArguments().getString("courseDescription");
        }

        if (courseId == null) {
            Toast.makeText(getContext(),
                    "Error: Course ID not found",
                    Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return null; // Return null since no view is inflated
        }

        // Initialize views
        courseNameTV = view.findViewById(R.id.courseName);
        courseDescriptionTV = view.findViewById(R.id.courseDescription);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set course details
        courseNameTV.setText(courseName);
        courseDescriptionTV.setText(courseDescription);

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Setup ViewPager and TabLayout
        setupViewPager();

        return view;
    }

    private void setupViewPager() {
        LearningPagerAdapter pagerAdapter = new LearningPagerAdapter(requireActivity());
        pagerAdapter.addFragment(VideosFragment.newInstance(courseId), "Videos");
        pagerAdapter.addFragment(PracticesFragment.newInstance(courseId), "Practice");
        pagerAdapter.addFragment(QuizzesFragment.newInstance(courseId), "Quiz");

        viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
        ).attach();

        // Set the initial text color for the tabs
        tabLayout.setTabTextColors(
                ContextCompat.getColor(requireContext(), R.color.dark_gray),
                ContextCompat.getColor(requireContext(), R.color.blue_others_CS)
        );

        // Set TabLayout's indicator color
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.blue_others_CS));

        // Handle tab selection events
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TabView", "Child count1: " + tab.view.getChildCount());
                // Change color for the selected tab
                TextView tabTextView = (TextView) tab.view.getChildAt(1);
                if (tabTextView != null) {
                    tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18); // Set text size for selected tab
                    tabTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_others_CS)); // Set text color for selected tab
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("TabView", "Child count2: " + tab.view.getChildCount());
                // Change color for unselected tab
                TextView tabTextView = (TextView) tab.view.getChildAt(1);
                if (tabTextView != null) {
                    tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15); // Reset text size
                    tabTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_gray)); // Reset text color
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optionally handle reselection
            }
        });
    }
}