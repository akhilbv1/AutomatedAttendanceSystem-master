package com.projects.automatedattendancesystem;

import android.app.Application;

public class AttendanceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.loadPreferences(this);
    }
}
