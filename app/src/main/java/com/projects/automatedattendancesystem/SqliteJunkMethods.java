/*
package com.projects.automatedattendancesystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projects.automatedattendancesystem.Sqlite.SqliteHelper;
import com.projects.automatedattendancesystem.Tables.Class;
import com.projects.automatedattendancesystem.Tables.School;
import com.projects.automatedattendancesystem.Tables.Student;
import com.projects.automatedattendancesystem.Tables.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteJunkMethods  {

    public int getClassCount(String tableName) {
        String countQuery = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public List<Student> getClassesListByClassName(String className) {
        List<Student> studentsList = new ArrayList<>();

        //  String query = " SELECT * FROM "+ Student.TABLE_NAME + " WHERE "+ Student.COLUMN_CLASS_NAME + " = " + " 'CSE' " ;

        SQLiteDatabase sqLiteDatabases = this.getReadableDatabase();
        //   Cursor cursor = sqLiteDatabases.rawQuery(query,null);
        Cursor cursor = sqLiteDatabases.query(Student.TABLE_NAME, new String[]{"*"}, Student.COLUMN_CLASS_NAME + " =? ", new String[]{className}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setSlNo(cursor.getString(cursor.getColumnIndex(Student.COLUMN_SlNO)));
                student.setStud_Id(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_ID)));
                student.setStud_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_NAME)));
                student.setFather_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_FATHER_NAME)));
                student.setClass_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
                studentsList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabases.close();
        return studentsList;
    }

    public List<Class> getTimingsListByClassName(String className) {
        List<Class> classList = new ArrayList<>();

        String query = " SELECT * FROM " + className;

        SQLiteDatabase sqLiteDatabases = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabases.rawQuery(query, null);
        //  Cursor cursor = sqLiteDatabases.query(Student.TABLE_NAME,new String[]{"*"},Student.COLUMN_CLASS_NAME+" =? ",new String[]{className},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Class aClass = new Class();
                aClass.setSlNo(cursor.getInt(cursor.getColumnIndex(Class.COLUMN_SLNO)));
                aClass.setCheckInDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_DATE)));
                aClass.setCheckOutDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_DATE)));
                aClass.setCheckInTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_TIME)));
                aClass.setCheckOutTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_TIME)));
                aClass.setStudId(cursor.getString(cursor.getColumnIndex(Class.COLUMN_STUD_ID)));
                classList.add(aClass);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabases.close();
        return classList;
    }

    public long insertIntoStudent(Student student) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_SlNO, student.getSlNo());
        values.put(Student.COLUMN_STUD_ID, student.getStud_Id());
        values.put(Student.COLUMN_STUD_NAME, student.getStud_Name());
        values.put(Student.COLUMN_FATHER_NAME, student.getFather_Name());
        values.put(Student.COLUMN_CLASS_NAME, student.getClass_Name());
        values.put(Student.COLUMN_PHONE, student.getPhone());
        long id = db.insert(Student.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public long insertIntoStudent(User user) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(User.COLUMN_SLNO, user.getSlNo());
        values.put(User.COLUMN_OLD_PASSWORD, user.getOldPassword());
        values.put(User.COLUMN_NEW_PASSWORD, user.getNewPassword());
        values.put(User.COLUMN_TIME, user.getTime());
        long id = db.insert(User.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Student.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setSlNo(String.valueOf(cursor.getInt(cursor.getColumnIndex(Student.COLUMN_SlNO))));
                student.setStud_Id(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_ID)));
                student.setClass_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
                student.setFather_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_FATHER_NAME)));
                student.setStud_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_NAME)));
                student.setPhone(cursor.getString(cursor.getColumnIndex(Student.COLUMN_PHONE)));
                studentList.add(student);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();
        cursor.close();
        return studentList;
    }

    public List<School> getAllSchools() {
        List<School> schoolList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + School.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                School school = new School();
                school.setSlNo(cursor.getInt(cursor.getColumnIndex(School.COLUMN_SlNO)));
                school.setSchl_Name(cursor.getString(cursor.getColumnIndex(School.COLUMN_SCHLNAME)));
                school.setSchl_Addres(cursor.getString(cursor.getColumnIndex(School.COLUMN_SCHLADDRESS)));
                school.setSchl_Contact_Number(cursor.getInt(cursor.getColumnIndex(School.COLUMN_CONTACT)));
                schoolList.add(school);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();
        cursor.close();
        return schoolList;
    }

    public List<Class> getAllClassDetails(String tableName) {
        List<Class> classList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + School.TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Class class_Pojo = new Class();
                class_Pojo.setSlNo(cursor.getInt(cursor.getColumnIndex(Class.COLUMN_SLNO)));
                class_Pojo.setStudId(cursor.getString(cursor.getColumnIndex(Class.COLUMN_STUD_ID)));
                class_Pojo.setCheckInDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_DATE)));
                class_Pojo.setCheckOutDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_DATE)));
                class_Pojo.setCheckInTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_TIME)));
                class_Pojo.setCheckOutTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_TIME)));
                classList.add(class_Pojo);
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return classList;
    }

    public Map<String, Class> getAttendanceBydates(String tableName, long fromDate, long toDate) {
        Map<String, Class> attendanceList = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        // String query = " SELECT * FROM " + tableName + " WHERE " + Class.COLUMN_CHECKIN_DATE_MILLI + " BETWEEN " + fromDate + " AND " + toDate + " ORDER BY "+Class.COLUMN_CHECKIN_DATE_MILLI+" ASC ";
        String query = " SELECT * FROM " + tableName;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        do {
            Class class_pojo = new Class();
            class_pojo.setSlNo(cursor.getInt(cursor.getColumnIndex(Class.COLUMN_SLNO)));
            class_pojo.setStudId(cursor.getString(cursor.getColumnIndex(Class.COLUMN_STUD_ID)));
            class_pojo.setCheckInDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_DATE)));
            class_pojo.setCheckOutDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_DATE)));
            class_pojo.setCheckInTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_TIME)));
            class_pojo.setCheckOutTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_TIME)));
            attendanceList.put(Utils.convertDate(cursor.getLong(cursor.getColumnIndex(Class.COLUMN_STUD_ID))), class_pojo);
        } while (cursor.moveToNext());
        cursor.close();
        sqLiteDatabase.close();
        return attendanceList;
    }

    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + School.TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setSlNo(cursor.getInt(cursor.getColumnIndex(User.COLUMN_SLNO)));
                user.setOldPassword(cursor.getString(cursor.getColumnIndex(User.COLUMN_OLD_PASSWORD)));
                user.setNewPassword(cursor.getString(cursor.getColumnIndex(User.COLUMN_NEW_PASSWORD)));
                user.setTime(cursor.getLong(cursor.getColumnIndex(User.COLUMN_TIME)));
                usersList.add(user);
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return usersList;
    }

}
*/
