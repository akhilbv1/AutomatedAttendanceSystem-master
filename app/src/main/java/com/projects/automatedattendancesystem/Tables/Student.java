package com.projects.automatedattendancesystem.Tables;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {

    public static final String TABLE_NAME = "Students";
    public static final String COLUMN_SlNO = "SlNo";
    public static final String COLUMN_STUD_ID = "Stud_Id";
    public static final String COLUMN_STUD_NAME = "Stud_Name";
    public static final String COLUMN_FATHER_NAME = "Father_Name";
    public static final String COLUMN_CLASS_NAME = "Class_Name";
    public static final String COLUMN_PHONE = "Phone";

    public static String createTableStudentQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SlNO + " INTEGER,"
                + COLUMN_STUD_ID + " TEXT PRIMARY KEY,"
                + COLUMN_STUD_NAME + " TEXT,"
                + COLUMN_FATHER_NAME + " TEXT,"
                + COLUMN_CLASS_NAME + " TEXT,"
                + COLUMN_PHONE + " INTEGER "
                + ")";
    }

    private String SlNo,Phone;

    private String Stud_Id,Stud_Name,Father_Name,Class_Name;

    public String getSlNo() {
        return SlNo;
    }

    public void setSlNo(String slNo) {
        SlNo = slNo;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getStud_Id() {
        return Stud_Id;
    }

    public void setStud_Id(String stud_Id) {
        Stud_Id = stud_Id;
    }

    public String getStud_Name() {
        return Stud_Name;
    }

    public void setStud_Name(String stud_Name) {
        Stud_Name = stud_Name;
    }

    public String getFather_Name() {
        return Father_Name;
    }

    public void setFather_Name(String father_Name) {
        Father_Name = father_Name;
    }

    public String getClass_Name() {
        return Class_Name;
    }

    public void setClass_Name(String class_Name) {
        Class_Name = class_Name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.SlNo);
        dest.writeString(this.Phone);
        dest.writeString(this.Stud_Id);
        dest.writeString(this.Stud_Name);
        dest.writeString(this.Father_Name);
        dest.writeString(this.Class_Name);
    }

    public Student() {
    }

    protected Student(Parcel in) {
        this.SlNo = in.readString();
        this.Phone = in.readString();
        this.Stud_Id = in.readString();
        this.Stud_Name = in.readString();
        this.Father_Name = in.readString();
        this.Class_Name = in.readString();
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
