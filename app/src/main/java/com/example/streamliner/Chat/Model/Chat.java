package com.example.streamliner.Chat.Model;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    private String chatId;
    private String type; // "private" or "group"
    private String lastMessage;
    private long lastMessageTime;
    private Map<String, Boolean> participants;
    private String groupName; // null for private chats

    public Chat() {
        participants = new HashMap<>();
    }

    public Chat(String type) {
        this.type = type;
        this.participants = new HashMap<>();
        this.lastMessageTime = System.currentTimeMillis();
    }

    // Getters and setters
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
