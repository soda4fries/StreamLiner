package com.example.streamliner.Chat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Chat.Adapter.ChatListAdapter;
import com.example.streamliner.Chat.Model.Chat;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {
    private FragmentChatsBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private ChatListAdapter adapter;
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
        String currentUserId = auth.getCurrentUser().getUid();
        adapter = new ChatListAdapter(chatsList, chat -> {
            Bundle args = new Bundle();
            args.putString("chatId", chat.getChatId());
            args.putString("chatType", chat.getType());
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_chatsFragment_to_chatFragment, args);
        }, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatsRecyclerView.setLayoutManager(layoutManager);
        binding.chatsRecyclerView.setAdapter(adapter);

        // Add divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.chatsRecyclerView.getContext(),
                layoutManager.getOrientation()
        );
        binding.chatsRecyclerView.addItemDecoration(dividerItemDecoration);
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

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
