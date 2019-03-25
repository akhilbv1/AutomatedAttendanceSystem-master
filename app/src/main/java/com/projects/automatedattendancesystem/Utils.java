package com.projects.automatedattendancesystem;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int NORESULT = 3;


    //error codes for checkin
    public static final int NOSTUDENTID = 4;
    public static final int ALREADYEXISTS = 5;
    public static final int NOCHECKINDATE = 6;


    //table exists or not
    public static final int TABLE_EXISTS = 9;
    public static final int TABLE_DOESNOT_EXISTS = 10;

    static final int NOSTUDENTSEXIST = 11;


    //api key for teleo
    static final String SMS_API_KEY = "1vhKRku3pDk-GQnlto8GpUdT7bd5Uw6YLShZ05RqNj";



    public static boolean checkAndRequestPermissions(Activity context, int PERMISSION_REQUEST_ID) {
        int permissionSendMessage = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int readStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_ID);
            return false;
        }
        return true;
    }

    //22-02-2018
    public static List<String> getDates(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(android.text.format.DateFormat.format("dd MMM", cal1.getTime()).toString());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static String convertDate(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM");
        return simpleDateFormat.format(calendar);
    }

    public static String convertDate(String date) {
        SimpleDateFormat fromDate = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = null;
        try {
            date1 = fromDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM");
        return simpleDateFormat.format(date1);
    }

    public static String getMessage(String studentName, String collegeName) {
        return studentName + " was absent today without prior notice.Please send your child with the Leave Letter.- " + collegeName;
    }

    public static String getInMessage(String studentName, String inTime, String colgName) {
        return "Dear parent, your child " + studentName + " is signed in the college/ school at " + inTime + " - " + colgName;
    }

    public static String getOutMessage(String studentName, String inTime, String colgName) {
        return "Dear parent, your child " + studentName + " is signed out from the college/ school at " + inTime + " - " + colgName;
    }

    public static String getCheckInOutMessage(String studentName, String inTime, String colgName) {
        return "Dear parent, your child " + studentName + " is signed at " + inTime + " - " + colgName;
    }

    public static boolean compareDates(String date1, String date2) {
        boolean isSame = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date ObjDate1 = simpleDateFormat.parse(date1);
            Date ObjDate2 = simpleDateFormat.parse(date2);
            isSame = ObjDate1.equals(ObjDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isSame;
    }


}

