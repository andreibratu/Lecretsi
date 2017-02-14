package com.glimpse.lecretsi;

public class Conversation {

    private User user;
    private String lastMessage;
    private Long lastMessageDate;

    //default constructor needed for the database
    public Conversation() {

    }

    public Conversation(User user, String lastMessage, Long lastMessageDate) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
