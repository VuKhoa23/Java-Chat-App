package com.vukhoa23.app.client.entity;

import java.io.Serializable;

public class FileSend implements Serializable {
    private String originalName;
    private String generatedName;

    public FileSend(String originalName, String generatedName) {
        this.originalName = originalName;
        this.generatedName = generatedName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getGeneratedName() {
        return generatedName;
    }

    public void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    @Override
    public String toString() {
        return "FileSend{" +
                "originalName='" + originalName + '\'' +
                ", generatedName='" + generatedName + '\'' +
                '}';
    }
}
