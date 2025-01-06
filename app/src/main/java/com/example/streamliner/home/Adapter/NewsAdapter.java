package com.example.streamliner.home.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.databinding.ItemNewsBinding;
import com.example.streamliner.home.Item.NewsItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NewsAdapter extends ListAdapter<NewsItem, NewsAdapter.ViewHolder> {


    public NewsAdapter() {
        super(new DiffUtil.ItemCallback<NewsItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull NewsItem oldItem, @NonNull NewsItem newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull NewsItem oldItem, @NonNull NewsItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewsBinding binding = ItemNewsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNewsBinding binding;

        ViewHolder(ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NewsItem item) {
            binding.newsTitleTextView.setText(item.getTitle());
            binding.newsDescriptionTextView.setText(item.getDescription());

            // Format timestamp to readable date
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(new Date(item.getTimestamp()));
            binding.newsDateTextView.setText(formattedDate);
        }
    }
}
