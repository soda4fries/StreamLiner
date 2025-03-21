package com.example.streamliner.Chat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Chat.Adapter.MessagesAdapter;
import com.example.streamliner.Chat.Model.Message;
import com.example.streamliner.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private MessagesAdapter adapter;
    private List<Message> messagesList;
    private String chatId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        messagesList = new ArrayList<>();

        assert getArguments() != null;
        chatId = getArguments().getString("chatId");
        setupToolbar();
        setupRecyclerView();
        setupMessageInput();
        loadMessages();

        return binding.getRoot();
    }

    private void setupToolbar() {
        String chatType = getArguments().getString("chatType");
        if (chatType.equals("group")) {
            database.child("chats").child(chatId).get()
                    .addOnSuccessListener(snapshot -> {
                        String groupName = snapshot.child("groupName").getValue(String.class);
                        binding.toolbar.setTitle(groupName);
                    });
        } else {
            // Get other user's ID from participants
            database.child("chats").child(chatId).child("participants")
                    .get().addOnSuccessListener(snapshot -> {
                        String currentUserId = auth.getCurrentUser().getUid();
                        for (DataSnapshot participant : snapshot.getChildren()) {
                            String userId = participant.getKey();
                            if (!userId.equals(currentUserId)) {
                                // Get username from Firestore
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(userId).get()
                                        .addOnSuccessListener(doc -> {
                                            String username = doc.getString("name");
                                            binding.toolbar.setTitle(username);
                                        });
                                break;
                            }
                        }
                    });
        }
    }

    private void setupRecyclerView() {
        adapter = new MessagesAdapter(messagesList, auth.getCurrentUser().getUid());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        binding.messagesRecyclerView.setLayoutManager(layoutManager);
        binding.messagesRecyclerView.setAdapter(adapter);
    }

    private void setupMessageInput() {
        binding.sendButton.setOnClickListener(v -> {
            String messageText = binding.messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                binding.messageInput.setText("");
            }
        });
    }

    private void loadMessages() {
        database.child("messages").child(chatId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesList.clear();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            Message message = messageSnapshot.getValue(Message.class);
                            if (message != null) {
                                message.setMessageId(messageSnapshot.getKey());
                                messagesList.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void sendMessage(String content) {
        String userId = auth.getCurrentUser().getUid();
        Message message = new Message(userId, content);

        DatabaseReference messagesRef = database.child("messages").child(chatId);
        String messageId = messagesRef.push().getKey();

        if (messageId != null) {
            message.setMessageId(messageId);
            messagesRef.child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        updateChatLastMessage(content);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this.getContext(), "Error Fetching Chat Message", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateChatLastMessage(String lastMessage) {
        DatabaseReference chatRef = database.child("chats").child(chatId);
        chatRef.child("lastMessage").setValue(lastMessage);
        chatRef.child("lastMessageTime").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}