package com.projects.automatedattendancesystem.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class SendMessagePojo implements Parcelable {
    private String message;

    private String phoneNumber;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeString(this.phoneNumber);
    }

    public SendMessagePojo() {
    }

    protected SendMessagePojo(Parcel in) {
        this.message = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Parcelable.Creator<SendMessagePojo> CREATOR = new Parcelable.Creator<SendMessagePojo>() {
        @Override
        public SendMessagePojo createFromParcel(Parcel source) {
            return new SendMessagePojo(source);
        }

        @Override
        public SendMessagePojo[] newArray(int size) {
            return new SendMessagePojo[size];
        }
    };
}
