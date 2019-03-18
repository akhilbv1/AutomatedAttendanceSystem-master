package com.projects.automatedattendancesystem.Tables;

public class StudentAttendanceStatus {

    public static final String TABLE_NAME = "StudentAttendanceStatus";

    public static final String COLUMN_ISCHECKIN = "IsCheckIn";

    public static final String COLUMN_STUDID = "Student_ID";

    public static final String COLUMN_STUD_NUMBER = "Student_Number";

    public static final String COLUMN_STUD_NAME = "Student_Name";

    public static final String COLUMN_SLNO = "SlNo";

    private boolean isCheckIn;

    private String StudId, StudName, Stud_Number;

    public static String getStudentAttendanceQuery() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SLNO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ISCHECKIN + " INTEGER DEFAULT 0,"
                + COLUMN_STUD_NUMBER + " TEXT,"
                + COLUMN_STUD_NAME + " TEXT,"
                + COLUMN_STUDID + " TEXT"
                + ")";
    }

    public boolean getIsCheckIn() {
        return isCheckIn;
    }

    public void setIsCheckIn(boolean isCheckIn) {
        this.isCheckIn = isCheckIn;
    }

    public String getStudId() {
        return StudId;
    }

    public void setStudId(String studId) {
        StudId = studId;
    }

    public String getStudName() {
        return StudName;
    }

    public void setStudName(String studName) {
        StudName = studName;
    }

    public String getStud_Number() {
        return Stud_Number;
    }

    public void setStud_Number(String stud_Number) {
        Stud_Number = stud_Number;
    }
}
