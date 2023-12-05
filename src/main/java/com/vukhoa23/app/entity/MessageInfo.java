package com.vukhoa23.app.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    private String username;
    private String message;

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public MessageInfo(String username, String message) {
        this.username = username;
        this.message = message;
    }

    @Override
    public String toString() {
        return username + ": " + message;
    }
}
