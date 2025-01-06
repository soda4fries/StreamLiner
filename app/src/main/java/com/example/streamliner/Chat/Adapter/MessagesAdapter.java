package com.example.streamliner.Chat.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.Chat.Model.Message;
import com.example.streamliner.databinding.ItemMessageBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private final String currentUserId;
    private final SimpleDateFormat timeFormat;

    public MessagesAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding binding = ItemMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemMessageBinding binding;

        MessageViewHolder(ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.messageText.setText(message.getContent());
            binding.messageTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.messageCard.getLayoutParams();

            Log.d("MessagesAdapter", "Message sender ID: " + message.getSenderId() + ", Current user ID: " + currentUserId);

            if (message.getSenderId().equals(currentUserId)) {
                // Align to end (right)
                params.startToStart = ConstraintLayout.LayoutParams.UNSET;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            } else {
                // Align to start (left)
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            }

            binding.messageCard.setLayoutParams(params);
        }

    }
}