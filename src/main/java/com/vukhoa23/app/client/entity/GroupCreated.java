package com.vukhoa23.app.client.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupCreated implements Serializable {
    public ArrayList<String> usersInGroup = new ArrayList<>();

    public ArrayList<String> getUsersInGroup() {
        return usersInGroup;
    }

    public void setUsersInGroup(ArrayList<String> usersInGroup) {
        this.usersInGroup = usersInGroup;
    }
}
