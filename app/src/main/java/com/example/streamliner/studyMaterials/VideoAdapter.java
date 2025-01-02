package com.example.streamliner.studyMaterials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamliner.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final List<Video> videos;
    private final OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    public VideoAdapter(List<Video> videos, OnVideoClickListener listener) {
        this.videos = videos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.titleTV.setText(video.getTitle());

        // Load thumbnail using Glide
        Glide.with(holder.itemView.getContext())
                .load(video.getThumbnailUrl())
                .placeholder(R.color.course_desc)
                .error(R.color.red_text)
                .into(holder.thumbnailIV);

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailIV;
        TextView titleTV;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailIV = itemView.findViewById(R.id.thumbnailIV);
            titleTV = itemView.findViewById(R.id.titleTV);
        }
    }
}
