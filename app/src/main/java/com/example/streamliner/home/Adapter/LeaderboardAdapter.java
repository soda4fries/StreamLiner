package com.example.streamliner.home.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.R;
import com.example.streamliner.databinding.ItemLeaderboardHomeBinding;
import com.example.streamliner.home.Item.LeaderboardItem;

public class LeaderboardAdapter extends ListAdapter<LeaderboardItem, LeaderboardAdapter.ViewHolder> {
    public LeaderboardAdapter() {
        super(new DiffUtil.ItemCallback<LeaderboardItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull LeaderboardItem oldItem, @NonNull LeaderboardItem newItem) {
                return oldItem.getUserId().equals(newItem.getUserId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull LeaderboardItem oldItem, @NonNull LeaderboardItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardHomeBinding binding = ItemLeaderboardHomeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), position + 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardHomeBinding binding;

        ViewHolder(ItemLeaderboardHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LeaderboardItem item, int position) {
            binding.rankTextView.setText(String.valueOf(position));
            binding.usernameTextView.setText(item.getUsername());
            binding.pointsTextView.setText(String.format("%d pts", item.getPoints()));

            // Set medal colors for top 3
            if (position <= 3) {
                binding.rankTextView.setVisibility(View.GONE);
                binding.medalImage.setVisibility(View.VISIBLE);
                int medalResource = position == 1 ? R.drawable.gold_medal :
                        position == 2 ? R.drawable.silver_medal :
                                R.drawable.bronze_medal;
                binding.medalImage.setImageResource(medalResource);
            } else {
                binding.rankTextView.setVisibility(View.VISIBLE);
                binding.medalImage.setVisibility(View.GONE);
            }
        }
    }
}