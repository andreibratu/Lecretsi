package com.glimpse.lecretsi;


public class ChatMessage {

    private String id;
    private String text;
    private String date;
    private String time;

    public ChatMessage() {

    }

    public ChatMessage(String id, String text, String date, String time) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.time = time;
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
    
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
