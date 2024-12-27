package com.example.streamliner;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

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

        return view;
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
            Intent intent = new Intent(getActivity(), FilterResultsActivity.class);
            intent.putExtra("searchQuery", searchQuery);
            startActivity(intent);
        }
    }

    private void applyFilters() {
        List<String> selectedSubjects = filterAdapter.getSelectedSubjects();
        if (!selectedSubjects.isEmpty()) {
            Intent intent = new Intent(getActivity(), FilterResultsActivity.class);
            intent.putStringArrayListExtra("selectedSubjects", new ArrayList<>(selectedSubjects));
            startActivity(intent);
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