package com.glimpse.lecretsi;


public class ChatMessage {

    private String id;
    private String name;
    private String email;
    private String text;
    private String date_time;

    public ChatMessage() {
    }

    public ChatMessage(String name, String email, String text, String date_time) {
        this.name = name;
        this.email = email;
        this.text = text;
        this.date_time = date_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setDateTime(String date_time) {
        this.date_time = date_time;
    }

    public String getDateTime() {
        return date_time;
    }
}
