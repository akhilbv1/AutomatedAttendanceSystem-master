package com.projects.automatedattendancesystem.Pojo;

import com.google.gson.annotations.SerializedName;

public class MessagePojo {

    @SerializedName("text")
    private String message;

    @SerializedName("number")
    private String mobileNum;

    public MessagePojo(String message, String mobileNum) {
        this.message = message;
        this.mobileNum = mobileNum;
    }

    public MessagePojo(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
}
