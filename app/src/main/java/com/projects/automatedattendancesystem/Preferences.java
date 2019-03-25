package com.projects.automatedattendancesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static String SCHOOL_NAME = "Automated Attendance System";

    static void savePreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("SchoolName",SCHOOL_NAME);
        editor.apply();
    }

    static void loadPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SCHOOL_NAME = preferences.getString("SchoolName","");
    }
}
