package com.projects.automatedattendancesystem.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TagsStatePojo implements Parcelable {
    private String Stud_Id;

    private boolean isSelected = false;

    public TagsStatePojo(String stud_Id, boolean isSelected) {
        Stud_Id = stud_Id;
        this.isSelected = isSelected;
    }

    public String getStud_Id() {
        return Stud_Id;
    }

    public void setStud_Id(String stud_Id) {
        Stud_Id = stud_Id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Stud_Id);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public TagsStatePojo() {
    }

    protected TagsStatePojo(Parcel in) {
        this.Stud_Id = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TagsStatePojo> CREATOR = new Parcelable.Creator<TagsStatePojo>() {
        @Override
        public TagsStatePojo createFromParcel(Parcel source) {
            return new TagsStatePojo(source);
        }

        @Override
        public TagsStatePojo[] newArray(int size) {
            return new TagsStatePojo[size];
        }
    };
}
