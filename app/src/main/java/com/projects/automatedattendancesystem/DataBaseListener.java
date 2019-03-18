package com.projects.automatedattendancesystem;

public interface DataBaseListener {
    void onComplete(String tableName,Object result);
    void onStart();
}
