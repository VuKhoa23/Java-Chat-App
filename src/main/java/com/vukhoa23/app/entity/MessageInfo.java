package com.vukhoa23.app.entity;

import java.io.Serializable;
import java.util.Date;

public class MessageInfo implements Serializable {
    private String username;
    private String receiver;
    private String message;
    private String createdDate;
    private int isGroupChat;
    private int groupId;

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(int groupChat) {
        isGroupChat = groupChat;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public MessageInfo(String username, String receiver, String message, String createdDate) {
        this.username = username;
        this.receiver = receiver;
        this.message = message;
        this.createdDate = createdDate;
        this.isGroupChat = 0;
    }

    public MessageInfo(String username, String message, String createdDate, int isGroupChat, int groupId) {
        this.username = username;
        this.message = message;
        this.createdDate = createdDate;
        this.isGroupChat = isGroupChat;
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return username + ": " + message;
    }
}
