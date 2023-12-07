package com.vukhoa23.app.entity;

import java.io.Serializable;
import java.util.Date;

public class MessageInfo implements Serializable {
    private String username;
    private String receiver;
    private String message;
    private String createdDate;

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

    public MessageInfo(String username, String message, String createdDate) {
        this.username = username;
        this.message = message;
        this.createdDate = createdDate;
    }

    public MessageInfo(String username, String receiver, String message, String createdDate) {
        this.username = username;
        this.receiver = receiver;
        this.message = message;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return username + ": " + message;
    }
}
