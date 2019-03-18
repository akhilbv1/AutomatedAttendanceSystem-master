package com.projects.automatedattendancesystem.Tables;

import android.os.Parcel;
import android.os.Parcelable;

public class




Class implements Parcelable {

    public static final String COLUMN_SLNO = "SlNo";
    public static final String COLUMN_CHECKIN_DATE = "CheckInDate";
    public static final String COLUMN_CHECKOUT_DATE = "CheckOutDate";
    public static final String COLUMN_CHECKIN_TIME = "CheckInTime";
    public static final String COLUMN_CHECKOUT_TIME = "CheckOutTime";
    public static final String COLUMN_CHECKIN_DATE_MILLI = "CheckInDateInMilliSec";

    public static final String COLUMN_STUD_ID = "StudId";

    private int SlNo;

    private String CheckInTime,CheckOutTime;

    private String checkInDate,checkOutDate;

    private long checkInDateMilli;

    public long getCheckInDateMilli() {
        return checkInDateMilli;
    }

    public void setCheckInDateMilli(long checkInDateMilli) {
        this.checkInDateMilli = checkInDateMilli;
    }

    private String StudId;

    public String getStudId() {
        return StudId;
    }

    public void setStudId(String studId) {
        StudId = studId;
    }

    public int getSlNo() {
        return SlNo;
    }

    public void setSlNo(int slNo) {
        SlNo = slNo;
    }

    public String getCheckInTime() {
        return CheckInTime;
    }

    public void setCheckInTime(String checkInTime) {
        CheckInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return CheckOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        CheckOutTime = checkOutTime;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.SlNo);
        dest.writeString(this.CheckInTime);
        dest.writeString(this.CheckOutTime);
        dest.writeString(this.checkInDate);
        dest.writeString(this.checkOutDate);
        dest.writeLong(this.checkInDateMilli);
        dest.writeString(this.StudId);
    }

    public Class() {
    }

    protected Class(Parcel in) {
        this.SlNo = in.readInt();
        this.CheckInTime = in.readString();
        this.CheckOutTime = in.readString();
        this.checkInDate = in.readString();
        this.checkOutDate = in.readString();
        this.checkInDateMilli = in.readLong();
        this.StudId = in.readString();
    }

    public static final Parcelable.Creator<Class> CREATOR = new Parcelable.Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel source) {
            return new Class(source);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };
}
