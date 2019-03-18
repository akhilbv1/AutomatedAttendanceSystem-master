package com.projects.automatedattendancesystem.Pojo;

import com.google.gson.annotations.SerializedName;
import com.projects.automatedattendancesystem.Pojo.MultipleSmsPojo;

import java.util.List;

public class MessageBody {

    @SerializedName("apikey")
    private String apiKey;

    @SerializedName("username")
    private String userName;

    @SerializedName("hash")
    private String hash;


    @SerializedName("data")
    private List<MultipleSmsPojo> data;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<MultipleSmsPojo> getData() {
        return data;
    }

    public void setData(List<MultipleSmsPojo> data) {
        this.data = data;
    }

    public MessageBody(){}

    public MessageBody(String apiKey, List<MultipleSmsPojo> data) {
        this.apiKey = apiKey;
        this.data = data;
    }
}
