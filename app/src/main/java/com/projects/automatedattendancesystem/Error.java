package com.projects.automatedattendancesystem;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Error {

    @SerializedName("errors")
    private List<ErrorResponse> errorResponse;


    @SerializedName("status")
    private String status;



    public List<ErrorResponse> getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(List<ErrorResponse> errorResponse) {
        this.errorResponse = errorResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
