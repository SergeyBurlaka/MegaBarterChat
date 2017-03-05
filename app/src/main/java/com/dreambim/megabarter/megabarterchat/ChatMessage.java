package com.dreambim.megabarter.megabarterchat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class ChatMessage implements IMessage{

    private String id;
    private String fromUID;
    private String toUID;
    private String text;
    private long timeStamp;
    private Users user;

    public ChatMessage() {
    }

    public ChatMessage(String text, String fromUID, String toUID) {
        this.text = text;
        this.fromUID = fromUID;
        this.toUID = toUID;

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

    public void setUser(Users user){
        this.user = user;
    }

    public IUser getUser(){
        return user;
    }

    public String getToUID() {
        return toUID;
    }

    public void setToUID(String toUID) {
        this.toUID = toUID;
    }

    public java.util.Map<String, String> getTimeStamp() {
        return ServerValue.TIMESTAMP;
    }

    public Date getCreatedAt() {
        return new Date(getTimeStampLong());
    }

    @Exclude
    public Long getTimeStampLong() {
        return timeStamp;
    }
    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
