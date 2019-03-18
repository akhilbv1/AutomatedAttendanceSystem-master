package com.projects.automatedattendancesystem.Tables;

import android.os.Parcel;
import android.os.Parcelable;

public class School implements Parcelable {

    public static final String TABLE_NAME = "Schools";
    public static final String COLUMN_SlNO = "SlNo";
    public static final String COLUMN_SCHLNAME = "SchoolName";
    public static final String COLUMN_SCHLADDRESS = "SchoolAddress";
    public static final String COLUMN_CONTACT = "SchoolcontactNumber";

    public static String createTableStudentQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SlNO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SCHLNAME + " TEXT,"
                + COLUMN_SCHLADDRESS + " TEXT,"
                + COLUMN_CONTACT + " TEXT "
                + ")";
    }

    private int SlNo;

    private String Schl_Name,Schl_Addres,Schl_Contact_Number;

    public int getSlNo() {
        return SlNo;
    }

    public void setSlNo(int slNo) {
        SlNo = slNo;
    }

    public String getSchl_Name() {
        return Schl_Name;
    }

    public void setSchl_Name(String schl_Name) {
        Schl_Name = schl_Name;
    }

    public String getSchl_Addres() {
        return Schl_Addres;
    }

    public void setSchl_Addres(String schl_Addres) {
        Schl_Addres = schl_Addres;
    }

    public String getSchl_Contact_Number() {
        return Schl_Contact_Number;
    }

    public void setSchl_Contact_Number(String schl_Contact_Number) {
        Schl_Contact_Number = schl_Contact_Number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.SlNo);
        dest.writeString(this.Schl_Name);
        dest.writeString(this.Schl_Addres);
        dest.writeString(this.Schl_Contact_Number);
    }

    public School() {
    }

    protected School(Parcel in) {
        this.SlNo = in.readInt();
        this.Schl_Name = in.readString();
        this.Schl_Addres = in.readString();
        this.Schl_Contact_Number = in.readString();
    }

    public static final Parcelable.Creator<School> CREATOR = new Parcelable.Creator<School>() {
        @Override
        public School createFromParcel(Parcel source) {
            return new School(source);
        }

        @Override
        public School[] newArray(int size) {
            return new School[size];
        }
    };
}
