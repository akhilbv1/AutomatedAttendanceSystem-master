package com.projects.automatedattendancesystem.Tables;

import retrofit2.http.PUT;

public class TestTable {


    public static final String COLUMN_SlNO = "SlNo";

    public static final String COLUMN_INTEGER = "Value";

    private int TestInteger;


    public static String createTableStudentQuery(String TABLE_NAME) {
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SlNO + " INTEGER PRIMARY KEY,"
                + COLUMN_INTEGER + " TEXT,"
                + ")";
    }

    public int getTestInteger() {
        return TestInteger;
    }

    public void setTestInteger(int testInteger) {
        TestInteger = testInteger;
    }
}
