package com.glimpse.lecretsi;


public class ChatMessage {

    private String id;
    private String text;
    private String date_time;

    public ChatMessage() {
    }

    public ChatMessage(String text, String date_time) {
        this.text = text;
        this.date_time = date_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
