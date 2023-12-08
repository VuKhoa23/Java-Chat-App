package com.vukhoa23.app.client.entity;

import java.io.Serializable;
import java.net.Socket;

public class SocketInfo implements Serializable {
    private Socket socket;
    private String username;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SocketInfo(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    @Override
    public String toString() {
        return username;
    }
}
