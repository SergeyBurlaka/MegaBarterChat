package com.dreambim.megabarter.megabarterchat;

import java.util.HashMap;
import java.util.Map;

public class ChatMessage {

    private String id;
    private String fromUID;
    private String toUID;
    private String text;
    private Map<String, String> timeStamp = new HashMap<>();
    private long date;

    public ChatMessage() {
    }

    public ChatMessage(String text, String fromUID, String toUID) {
        this.text = text;
        this.fromUID = fromUID;
        this.toUID = toUID;
       // this.timeStamp=ServerValue.TIMESTAMP;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFromUID() {
        return fromUID;
    }

    public void setFromUID(String fromUID) {
        this.fromUID = fromUID;
    }

    public String getToUID() {
        return toUID;
    }

    public void setToUID(String toUID) {
        this.toUID = toUID;
    }

    /*@Exclude
    public Long getTimeStampLong(){
        return timeStamp;
    }

    public Map getTimeStamp() {
        return timeStamp;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }*/
}
