package com.vukhoa23.app.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    private String query;
    private String username;
    private String receiver;
    private String message;
    private String createdDate;
    private int isGroupChat;
    private int groupId;
    private float fileSize;

    private int isFile = 0;

    private String originalFileName = null;
    private String generatedFileName = null;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

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

    public int getIsGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(int isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public int getIsFile() {
        return isFile;
    }

    public void setIsFile(int isFile) {
        this.isFile = isFile;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public void setGeneratedFileName(String generatedFileName) {
        this.generatedFileName = generatedFileName;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
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

    public MessageInfo(String username, String receiver, String message, String createdDate, int isFile, String originalFileName, String generatedFileName) {
        this.username = username;
        this.receiver = receiver;
        this.message = message;
        this.createdDate = createdDate;
        this.isGroupChat = 0;
        this.isFile = isFile;
        this.originalFileName = originalFileName;
        this.generatedFileName = generatedFileName;
    }

    public MessageInfo(String username, String message, String createdDate, int isGroupChat, int groupId, int isFile, String originalFileName, String generatedFileName) {
        this.username = username;
        this.message = message;
        this.createdDate = createdDate;
        this.isGroupChat = isGroupChat;
        this.groupId = groupId;
        this.isFile = isFile;
        this.originalFileName = originalFileName;
        this.generatedFileName = generatedFileName;
    }

    @Override
    public String toString() {
        return username + ": " + message;
    }
}
