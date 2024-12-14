package com.example.streamliner.Chat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Chat.Adapter.ChatsAdapter;
import com.example.streamliner.R;

import com.example.streamliner.Chat.Model.Chat;
import com.example.streamliner.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private ChatsAdapter adapter;
    private List<Chat> chatsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        chatsList = new ArrayList<>();

        setupRecyclerView();
        setupFab();
        loadChats();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ChatsAdapter(chatsList, chat -> {
            Bundle args = new Bundle();
            args.putString("chatId", chat.getChatId());
            args.putString("chatType", chat.getType());
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_chatsFragment_to_chatFragment, args);
        });

        binding.chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatsRecyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        binding.newChatFab.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_chatsFragment_to_newChatFragment);
        });
    }

    private void loadChats() {
        String userId = auth.getCurrentUser().getUid();
        database.child("user-chats").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatsList.clear();
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String chatId = chatSnapshot.getKey();
                            loadChatDetails(chatId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void loadChatDetails(String chatId) {
        database.child("chats").child(chatId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            chat.setChatId(chatId);
                            chatsList.add(chat);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
