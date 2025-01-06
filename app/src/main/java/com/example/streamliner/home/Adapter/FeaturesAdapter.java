package com.example.streamliner.home.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.databinding.ItemFeatureBinding;
import com.example.streamliner.home.Item.FeatureItem;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class FeaturesAdapter extends ListAdapter<FeatureItem, FeaturesAdapter.ViewHolder> {
    public FeaturesAdapter() {
        super(new DiffUtil.ItemCallback<FeatureItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull FeatureItem oldItem, @NonNull FeatureItem newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull FeatureItem oldItem, @NonNull FeatureItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeatureBinding binding = ItemFeatureBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFeatureBinding binding;

        ViewHolder(ItemFeatureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FeatureItem item) {
            binding.featureTitleTextView.setText(item.getTitle());
            binding.featureDescriptionTextView.setText(item.getDescription());

            // Format expected release date
            String formattedDate = "Expected: " + new SimpleDateFormat("MMM yyyy", Locale.getDefault())
                    .format(item.getExpectedRelease());
            binding.featureReleaseDateTextView.setText(formattedDate);
        }
    }
}