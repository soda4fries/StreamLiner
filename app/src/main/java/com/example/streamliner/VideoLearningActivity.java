package com.example.streamliner;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class VideoLearningActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private String courseId;
    private String courseName;
    private String courseDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_learning);

        // Get course details from intent
        courseId = getIntent().getStringExtra("courseId");
        courseName = getIntent().getStringExtra("courseName");
        courseDescription = getIntent().getStringExtra("courseDescription");

        if (courseId == null) {
            Toast.makeText(this, "Error: Course ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        TextView courseNameTV = findViewById(R.id.courseNameTV);
        TextView courseDescriptionTV = findViewById(R.id.courseDescriptionTV);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set course details
        courseNameTV.setText(courseName);
        courseDescriptionTV.setText(courseDescription);

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Setup ViewPager and TabLayout
        setupViewPager();
    }

    private void setupViewPager() {
        LearningPagerAdapter pagerAdapter = new LearningPagerAdapter(this);
        pagerAdapter.addFragment(VideosFragment.newInstance(courseId), "Videos");
        pagerAdapter.addFragment(new Fragment(), "Practice");  // Placeholder
        pagerAdapter.addFragment(new Fragment(), "Quiz");      // Placeholder

        viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
        ).attach();

        // Set the initial text color for the tabs
        tabLayout.setTabTextColors(
                ContextCompat.getColor(this, R.color.dark_gray),
                ContextCompat.getColor(this, R.color.blue_others_CS)
        );

        // Set TabLayout's indicator color
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.blue_others_CS));

        // Handle tab selection events
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Change text size and color for the selected tab
                TextView tabTextView = (TextView) ((LinearLayout) tab.view).getChildAt(1);
                if (tabTextView != null) {
                    //tabTextView.setTextSize(18); // Set text size for selected tab
                    tabTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_others_CS)); // Set text color for selected tab
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Change text size and color for unselected tab
                TextView tabTextView = (TextView) ((LinearLayout) tab.view).getChildAt(1);
                if (tabTextView != null) {
                    //tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15); // Reset text size
                    tabTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray)); // Reset text color
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optionally handle reselection
            }
        });
    }
}