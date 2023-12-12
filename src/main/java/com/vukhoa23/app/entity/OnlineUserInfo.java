package com.vukhoa23.app.entity;

import java.io.Serializable;

public class OnlineUserInfo implements Serializable {
    private int port;
    private String username;

    public OnlineUserInfo(int port, String username) {
        this.port = port;
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "OnlineUserInfo{" +
                "port='" + port + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
