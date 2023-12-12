package com.vukhoa23.app.client.entity;

import java.io.Serializable;

public class GroupQueryResult implements Serializable {
    int GroupId;
    String GroupName;

    public GroupQueryResult(int groupId, String groupName) {
        GroupId = groupId;
        GroupName = groupName;
    }

    public int getGroupId() {
        return GroupId;
    }

    public void setGroupId(int groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    @Override
    public String toString() {
        return "GroupQueryResult{" +
                "GroupId=" + GroupId +
                ", GroupName='" + GroupName + '\'' +
                '}';
    }
}
