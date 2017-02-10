package com.glimpse.lecretsi;



public class Conversation {

    private User user;
    private String lastMessage;
    private String lastMessageDate;

    public Conversation() {

    }

    public Conversation(User user, String lastMessage, String lastMessageDate) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }

    public String getLastMessage(){
        return lastMessage;
    }

    public void setLastMessageDate(String lastMessageDate){
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessageDate(){
        return lastMessageDate;
    }
}
