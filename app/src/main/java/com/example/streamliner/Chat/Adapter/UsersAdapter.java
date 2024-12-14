package com.example.streamliner.Chat.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.Auth.Model.User;
import com.example.streamliner.databinding.ItemUserBinding;

import java.util.List;
import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private List<User> users;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UsersAdapter(List<User> users, OnUserClickListener listener) {
        this.users = new ArrayList<>(users);
        this.listener = listener;
    }

    public void updateList(List<User> newUsers) {
        this.users = new ArrayList<>(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.userName.setText(user.getDisplayName());
            binding.userEmail.setText(user.getEmail());

            // Load user avatar if available
            if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                // You can use Glide here to load the image
                // Glide.with(binding.userAvatar).load(user.getPhotoUrl()).into(binding.userAvatar);
            }

            itemView.setOnClickListener(v -> listener.onUserClick(user));
        }
    }
}