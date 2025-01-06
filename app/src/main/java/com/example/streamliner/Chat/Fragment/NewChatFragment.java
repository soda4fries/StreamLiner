package com.example.streamliner.Chat.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Chat.Adapter.UsersAdapter;
import com.example.streamliner.Chat.Model.Chat;
import com.example.streamliner.Chat.Model.User;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentNewChatBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference realtimeDb;
    private FirebaseFirestore firestore;
    private UsersAdapter usersAdapter;
    private UsersAdapter groupUsersAdapter;
    private List<User> usersList;
    private Set<String> selectedUsers;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        initializeFirebase();
        setupViews();
        return binding.getRoot();
    }

    private void initializeFirebase() {
        auth = FirebaseAuth.getInstance();
        realtimeDb = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        usersList = new ArrayList<>();
        selectedUsers = new HashSet<>();
    }

    private void setupViews() {
        setupToolbar();
        setupTabs();
        setupRecyclerViews();
        setupSearchListeners();
        setupCreateGroupButton();
        loadUsers();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.privateChatLayout.setVisibility(View.VISIBLE);
                    binding.groupChatLayout.setVisibility(View.GONE);
                } else {
                    binding.privateChatLayout.setVisibility(View.GONE);
                    binding.groupChatLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerViews() {
        // Private chat users adapter
        usersAdapter = new UsersAdapter(usersList, this::createPrivateChat);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersRecyclerView.setAdapter(usersAdapter);

        // Group chat users adapter
        groupUsersAdapter = new UsersAdapter(usersList, user -> {
            if (!selectedUsers.contains(user.getUid())) {
                addUserToGroup(user);
            }
        });
        binding.groupUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.groupUsersRecyclerView.setAdapter(groupUsersAdapter);
    }

    private void setupSearchListeners() {
        binding.searchUsersInput.addTextChangedListener(new SimpleTextWatcher(text ->
                filterUsers(text, usersAdapter)));

        binding.searchGroupUsersInput.addTextChangedListener(new SimpleTextWatcher(text ->
                filterUsers(text, groupUsersAdapter)));
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final SearchCallback callback;

        SimpleTextWatcher(SearchCallback callback) {
            this.callback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            callback.onSearch(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}

        interface SearchCallback {
            void onSearch(String query);
        }
    }

    private void filterUsers(String query, UsersAdapter adapter) {
        List<User> filteredList = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();
        for (User user : usersList) {
            if (user.getDisplayName().toLowerCase().contains(lowercaseQuery) ||
                    user.getEmail().toLowerCase().contains(lowercaseQuery)) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    private void loadUsers() {
        String currentUserId = auth.getCurrentUser().getUid();
        firestore.collection("users")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        usersList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            User user = User.fromFirestore(document);
                            if (user != null && !user.getUid().equals(currentUserId)) {
                                usersList.add(user);
                            }
                        }
                        usersAdapter.notifyDataSetChanged();
                        groupUsersAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupCreateGroupButton() {
        binding.createGroupButton.setOnClickListener(v -> {
            String groupName = binding.groupNameInput.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a group name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedUsers.size() < 2) {
                Toast.makeText(getContext(), "Please select at least 2 users", Toast.LENGTH_SHORT).show();
                return;
            }
            createGroupChat(groupName);
        });
    }

    private void addUserToGroup(User user) {
        selectedUsers.add(user.getUid());
        Chip chip = new Chip(requireContext());
        chip.setText(user.getDisplayName());
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            selectedUsers.remove(user.getUid());
            binding.selectedUsersChipGroup.removeView(chip);
        });
        binding.selectedUsersChipGroup.addView(chip);
    }

    private void createPrivateChat(User otherUser) {
        String currentUserId = auth.getCurrentUser().getUid();
        String chatId = getChatId(currentUserId, otherUser.getUid());

        Chat newChat = new Chat("private");
        newChat.setChatId(chatId);
        Map<String, Boolean> participants = new HashMap<>();
        participants.put(currentUserId, true);
        participants.put(otherUser.getUid(), true);
        newChat.setParticipants(participants);

        realtimeDb.child("chats").child(chatId).setValue(newChat)
                .addOnSuccessListener(aVoid -> {
                    // Add chat reference to both users
                    realtimeDb.child("user-chats").child(currentUserId).child(chatId).setValue(true);
                    realtimeDb.child("user-chats").child(otherUser.getUid()).child(chatId).setValue(true);

                    // Navigate to chat
                    navigateToChat(chatId, "private");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create chat", Toast.LENGTH_SHORT).show());
    }

    private void createGroupChat(String groupName) {
        String chatId = realtimeDb.child("chats").push().getKey();
        if (chatId == null) return;

        String currentUserId = auth.getCurrentUser().getUid();
        Chat newChat = new Chat("group");
        newChat.setChatId(chatId);
        newChat.setGroupName(groupName);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put(currentUserId, true);
        for (String userId : selectedUsers) {
            participants.put(userId, true);
        }
        newChat.setParticipants(participants);

        realtimeDb.child("chats").child(chatId).setValue(newChat)
                .addOnSuccessListener(aVoid -> {
                    // Add chat reference to all participants
                    for (String userId : participants.keySet()) {
                        realtimeDb.child("user-chats").child(userId).child(chatId).setValue(true);
                    }

                    // Navigate to chat
                    navigateToChat(chatId, "group");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create group", Toast.LENGTH_SHORT).show());
    }

    private void navigateToChat(String chatId, String chatType) {
        Bundle args = new Bundle();
        args.putString("chatId", chatId);
        args.putString("chatType", chatType);
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_newChatFragment_to_chatFragment, args);
    }

    private String getChatId(String uid1, String uid2) {
        // Create a consistent chat ID for private chats
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}