package com.example.streamliner.courseDiscovery;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.streamliner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView filterRecyclerView;
    private Button applyFilterButton;
    private Button clearButton;
    private FilterAdapter filterAdapter;
    private List<String> subjects;
    private View filtersContainer;
    private int currentIndex;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(int currentIndex) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("currentIndex", currentIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getArguments() != null) {
            currentIndex = getArguments().getInt("currentIndex");
        }

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        filterRecyclerView = view.findViewById(R.id.filterRecyclerView);
        applyFilterButton = view.findViewById(R.id.applyFilterButton);
        clearButton = view.findViewById(R.id.clearButton);
        filtersContainer = view.findViewById(R.id.filtersContainer);

        // Initially hide the filters
        filtersContainer.setVisibility(View.GONE);

        // Setup subject list
        subjects = Arrays.asList("Biology", "Chemistry", "Physics", "Computer Science",  "Mathematics");

        // Setup RecyclerView
        filterAdapter = new FilterAdapter(subjects);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filterRecyclerView.setAdapter(filterAdapter);

        // Setup search functionality
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Setup focus change listener
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            filtersContainer.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            if (!hasFocus) {
                hideKeyboard();
            }
        });

        // Setup click listeners
        applyFilterButton.setOnClickListener(v -> applyFilters());
        clearButton.setOnClickListener(v -> clearFilters());

        // Handle clicks outside the search area
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (searchEditText.hasFocus()) {
                    searchEditText.clearFocus();
                    hideKeyboard();
                    return true;
                }
            }
            return false;
        });

        // Set up Top Courses Fragment
        setTopCourses();

        return view;
    }

    private void setTopCourses() {
        TopCoursesFragment fragment = TopCoursesFragment.newInstance(currentIndex);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.topCoursesContainer, fragment)
                .commit();
    }

    private void performSearch() {
        String searchQuery = searchEditText.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            // Clear focus and hide keyboard before starting new activity
            searchEditText.clearFocus();
            hideKeyboard();

            // Clear the search text
            searchEditText.setText("");

            // Start FilterResultsActivity
            /*Intent intent = new Intent(getActivity(), FilterResultsActivity.class);
            intent.putExtra("searchQuery", searchQuery);
            startActivity(intent);*/

            // Start FilterResultsFragment
            FilterResultsFragment fragment = FilterResultsFragment.newInstance(searchQuery, new ArrayList<String>(), 0);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer1, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void applyFilters() {
        List<String> selectedSubjects = filterAdapter.getSelectedSubjects();
        if (!selectedSubjects.isEmpty()) {
            FilterResultsFragment fragment = FilterResultsFragment.newInstance("", new ArrayList<>(selectedSubjects), 0);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer1, fragment)
                    .addToBackStack(null)
                    .commit();

            /*Intent intent = new Intent(getActivity(), FilterResultsActivity.class);
            intent.putStringArrayListExtra("selectedSubjects", new ArrayList<>(selectedSubjects));
            startActivity(intent);*/
        }
        searchEditText.clearFocus();
        hideKeyboard();
        //Toast.makeText(getContext(), "Applied filters: " + selectedSubjects.toString(), Toast.LENGTH_SHORT).show();
    }

    private void clearFilters() {
        searchEditText.setText("");
        filterAdapter.clearSelection();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}