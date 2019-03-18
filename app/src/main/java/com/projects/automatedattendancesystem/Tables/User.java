package com.projects.automatedattendancesystem.Tables;

public class User {

    public static final String TABLE_NAME = "User";
    public static final String COLUMN_OLD_PASSWORD = "OldPassword";
    public static final String COLUMN_NEW_PASSWORD = "NewPassword";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_SLNO = "SlNo";

    public static String createUserTableQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SLNO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_OLD_PASSWORD + " TEXT,"
                + COLUMN_NEW_PASSWORD + " TEXT,"
                + COLUMN_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
    }

    public static String initialiseUserPassword(){
        return " INSERT INTO "+ TABLE_NAME +"("
                + COLUMN_OLD_PASSWORD + " , "
                + COLUMN_NEW_PASSWORD + ") "
                +" VALUES (admin,admin)";
    }

    private int SlNo;

    private String oldPassword,NewPassword;

    private long time;

    public int getSlNo() {
        return SlNo;
    }

    public void setSlNo(int slNo) {
        SlNo = slNo;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
