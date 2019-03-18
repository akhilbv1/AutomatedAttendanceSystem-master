package com.projects.automatedattendancesystem.Pojo;

import com.google.gson.annotations.SerializedName;
import com.projects.automatedattendancesystem.TextMessagesListPojo;

import java.util.List;

public class MultipleSmsPojo {

    @SerializedName("sender")
    private String senderName;

    @SerializedName("messages")
    private List<MessagePojo> messagePojoList;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public List<MessagePojo> getMessagePojoList() {
        return messagePojoList;
    }

    public void setMessagePojoList(List<MessagePojo> messagePojoList) {
        this.messagePojoList = messagePojoList;
    }
}
