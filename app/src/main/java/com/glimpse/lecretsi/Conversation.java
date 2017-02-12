package com.glimpse.lecretsi;



public class Conversation {

    private User user;
    private String lastMessage;
    private Long lastMessageDate;

    public Conversation() {

    }

    public Conversation(User user, String lastMessage, Long lastMessageDate) {
        this.user = user;
        this.lastMessage = lastMessage;
        if(lastMessage != null) {
            this.lastMessageDate = lastMessageDate;
        } else {
            this.lastMessageDate = 0L;
        }
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

    public void setLastMessageDate(Long lastMessageDate){
        this.lastMessageDate = lastMessageDate;
    }

    public Long getLastMessageDate(){
        return lastMessageDate;
    }
}
