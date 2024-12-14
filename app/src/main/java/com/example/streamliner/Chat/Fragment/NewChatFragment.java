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

import com.example.streamliner.Auth.Model.User;
import com.example.streamliner.Chat.Adapter.UsersAdapter;
import com.example.streamliner.Chat.Model.Chat;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentNewChatBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private UsersAdapter usersAdapter;
    private UsersAdapter groupUsersAdapter;
    private List<User> usersList;
    private Set<String> selectedUsers;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        usersList = new ArrayList<>();
        selectedUsers = new HashSet<>();

        setupToolbar();
        setupTabs();
        setupRecyclerViews();
        setupSearchListeners();
        setupCreateGroupButton();
        loadUsers();

        return binding.getRoot();
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
        usersAdapter = new UsersAdapter(usersList, user -> createPrivateChat(user.getUid()));
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
        binding.searchUsersInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString(), usersAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.searchGroupUsersInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString(), groupUsersAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterUsers(String query, UsersAdapter adapter) {
        List<User> filteredList = new ArrayList<>();
        for (User user : usersList) {
            if (user.getDisplayName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    private void loadUsers() {
        String currentUserId = auth.getCurrentUser().getUid();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && !user.getUid().equals(currentUserId)) {
                        usersList.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
                groupUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPrivateChat(String otherUserId) {
        String currentUserId = auth.getCurrentUser().getUid();
        String chatId = getChatId(currentUserId, otherUserId);

        Chat newChat = new Chat("private");
        newChat.setChatId(chatId);
        Map<String, Boolean> participants = new HashMap<>();
        participants.put(currentUserId, true);
        participants.put(otherUserId, true);
        newChat.setParticipants(participants);

        database.child("chats").child(chatId).setValue(newChat)
                .addOnSuccessListener(aVoid -> {
                    // Add chat reference to both users
                    database.child("user-chats").child(currentUserId).child(chatId).setValue(true);
                    database.child("user-chats").child(otherUserId).child(chatId).setValue(true);

                    // Navigate to chat
                    Bundle args = new Bundle();
                    args.putString("chatId", chatId);
                    args.putString("chatType", "private");
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_newChatFragment_to_chatFragment, args);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create chat", Toast.LENGTH_SHORT).show());
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

    private void createGroupChat(String groupName) {
        String chatId = database.child("chats").push().getKey();
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

        database.child("chats").child(chatId).setValue(newChat)
                .addOnSuccessListener(aVoid -> {
                    // Add chat reference to all participants
                    for (String userId : participants.keySet()) {
                        database.child("user-chats").child(userId).child(chatId).setValue(true);
                    }

                    // Navigate to chat
                    Bundle args = new Bundle();
                    args.putString("chatId", chatId);
                    args.putString("chatType", "group");
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_newChatFragment_to_chatFragment, args);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create group", Toast.LENGTH_SHORT).show());
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