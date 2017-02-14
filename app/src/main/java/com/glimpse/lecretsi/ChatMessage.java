package com.glimpse.lecretsi;


public class ChatMessage {

    private String id;
    private String normalText;
    private String largonjiText;
    private String date;
    private String time;

    public ChatMessage() {

    }

    public ChatMessage(String id, String normalText, String largonjiText, String date, String time) {
        this.id = id;
        this.normalText = normalText;
        this.largonjiText = largonjiText;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNormalText() {
        return normalText;
    }

    public void setNormalText(String normalText) {
        this.normalText = normalText;
    }

    public void setLargonjiTextText(String largonjiText) {
        this.largonjiText = largonjiText;
    }

    public String getLargonjiText() {
        return largonjiText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
