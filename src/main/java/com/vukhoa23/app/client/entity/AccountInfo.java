package com.vukhoa23.app.client.entity;

import java.io.Serializable;

public class AccountInfo implements Serializable {
    String username;
    String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AccountInfo() {
        this.username = null;
        this.password = null;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
