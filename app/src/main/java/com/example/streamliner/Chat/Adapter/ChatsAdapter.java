package com.example.streamliner.Chat.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.streamliner.Chat.Model.Chat;
import com.example.streamliner.databinding.ItemChatBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    private final List<Chat> chats;
    private final OnChatClickListener listener;
    private final FirebaseFirestore firestore;
    private final String currentUserId;

    public ChatsAdapter(List<Chat> chats, OnChatClickListener listener, String currentUserId) {
        this.chats = chats;
        this.listener = listener;
        this.currentUserId = currentUserId;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = ItemChatBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatBinding binding;

        ChatViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Chat chat) {
            if (chat.getType().equals("group")) {
                binding.chatTitle.setText(chat.getGroupName());
            } else {

                String otherUserId = chat.getParticipants().keySet().stream()
                        .filter(id -> !id.equals(currentUserId))
                        .findFirst()
                        .orElse("");


                firestore.collection("users").document(otherUserId)
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String username = doc.getString("displayName");
                                binding.chatTitle.setText(username != null ? username : "Unknown User");
                            }
                        });
            }
            binding.lastMessage.setText(chat.getLastMessage());
            itemView.setOnClickListener(v -> listener.onChatClick(chat));
        }
    }
}