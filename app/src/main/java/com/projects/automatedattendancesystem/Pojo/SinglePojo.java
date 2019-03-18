package com.projects.automatedattendancesystem.Pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SinglePojo {

    @SerializedName("apikey")
    private String apikey;

    @SerializedName("numbers")
    private List<String> numbers;

    @SerializedName("sender")
    private String sender;

    @SerializedName("message")
    private String message;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        this.numbers = numbers;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
