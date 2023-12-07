package com.vukhoa23.app.entity;

import java.io.Serializable;
import java.util.Date;

public class MessageInfo implements Serializable {
    private String username;
    private String message;
    private Date createdDate;

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public MessageInfo(String username, String message, Date createdDate) {
        this.username = username;
        this.message = message;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return username + ": " + message;
    }
}
