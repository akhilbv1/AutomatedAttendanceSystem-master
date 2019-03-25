package com.projects.automatedattendancesystem.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projects.automatedattendancesystem.DataBaseListener;
import com.projects.automatedattendancesystem.Pojo.SendMessagePojo;
import com.projects.automatedattendancesystem.Preferences;
import com.projects.automatedattendancesystem.Tables.Class;
import com.projects.automatedattendancesystem.Tables.School;
import com.projects.automatedattendancesystem.Tables.Student;
import com.projects.automatedattendancesystem.Tables.StudentAttendanceStatus;
import com.projects.automatedattendancesystem.Tables.User;
import com.projects.automatedattendancesystem.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;


public class SqliteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 29;

    // Database Name
    private static final String DATABASE_NAME = "AttendanceSystem";


    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Student.createTableStudentQuery());
        db.execSQL(School.createTableStudentQuery());
        db.execSQL(User.createUserTableQuery());
        db.execSQL(StudentAttendanceStatus.getStudentAttendanceQuery());
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.COLUMN_OLD_PASSWORD, "admin");
        contentValues.put(User.COLUMN_NEW_PASSWORD, "admin");
        db.insert(User.TABLE_NAME, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + School.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        dropAllTables(db);

        // Create tables again
        onCreate(db);
    }

    private void dropAllTables(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }

        for (String table : tables) {
            if (table.startsWith("sqlite_") || table.startsWith(User.TABLE_NAME)) {
                continue;
            }
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
        c.close();
    }


    public Single<Integer> dropAllTables() {
        return Single.create(emitter -> {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                List<String> tables = new ArrayList<>();

                while (c.moveToNext()) {
                    tables.add(c.getString(0));
                }

                for (String table : tables) {
                    if (table.startsWith("sqlite_") && table.startsWith(User.TABLE_NAME)) {
                        continue;
                    }
                    String dropQuery = "DROP TABLE IF EXISTS " + table;
                    db.execSQL(dropQuery);
                }
                c.close();
                db.close();
                emitter.onSuccess(Utils.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });


    }



    public Single<List<StudentAttendanceStatus>> getAbsentStudents() {
        return Single.create(emitter -> {
            try {
                List<StudentAttendanceStatus> studentAttendanceStatuses = new ArrayList<>();
                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
                String query = " SELECT * FROM " + StudentAttendanceStatus.TABLE_NAME + " WHERE " + StudentAttendanceStatus.COLUMN_ISCHECKIN + " = 0";
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if (cursor.moveToNext()) {
                    do {
                        StudentAttendanceStatus studentAttendanceStatus = new StudentAttendanceStatus();
                        studentAttendanceStatus.setIsCheckIn(cursor.getInt(cursor.getColumnIndex(StudentAttendanceStatus.COLUMN_ISCHECKIN)) == 1);
                        studentAttendanceStatus.setStud_Number(cursor.getString(cursor.getColumnIndex(StudentAttendanceStatus.COLUMN_STUD_NUMBER)));
                        studentAttendanceStatus.setStudName(cursor.getString(cursor.getColumnIndex(StudentAttendanceStatus.COLUMN_STUD_NAME)));
                        studentAttendanceStatus.setStudId(cursor.getString(cursor.getColumnIndex(StudentAttendanceStatus.COLUMN_STUDID)));
                        studentAttendanceStatuses.add(studentAttendanceStatus);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                sqLiteDatabase.close();
                emitter.onSuccess(studentAttendanceStatuses);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }

    public Single<Integer> getStudentDetailsObserver(List<Student> studentList) {
        SQLiteDatabase db = this.getWritableDatabase();
        return Single.create(emitter -> {
            long result = Utils.NORESULT;
            db.beginTransaction();
            deleteAllValues(db);
            try {
                for (Student obj : studentList) {
                    ContentValues values = new ContentValues();
                    values.put(Student.COLUMN_SlNO, obj.getSlNo());
                    values.put(Student.COLUMN_STUD_ID, obj.getStud_Id());
                    values.put(Student.COLUMN_STUD_NAME, obj.getStud_Name());
                    values.put(Student.COLUMN_FATHER_NAME, obj.getFather_Name());
                    values.put(Student.COLUMN_CLASS_NAME, obj.getClass_Name());
                    values.put(Student.COLUMN_PHONE, obj.getPhone());
                    result = db.insert(Student.TABLE_NAME, null, values);
                    ContentValues StudentAttendanceStatusValues = new ContentValues();
                    StudentAttendanceStatusValues.put(StudentAttendanceStatus.COLUMN_STUDID, obj.getStud_Id());
                    StudentAttendanceStatusValues.put(StudentAttendanceStatus.COLUMN_STUD_NAME, obj.getStud_Name());
                    StudentAttendanceStatusValues.put(StudentAttendanceStatus.COLUMN_STUD_NUMBER, obj.getPhone());
                    db.insert(StudentAttendanceStatus.TABLE_NAME, null, StudentAttendanceStatusValues);
                }
                int status = result != -1 ? Utils.SUCCESS : Utils.FAILURE;
                emitter.onSuccess(status);
            } catch (Exception e) {
                emitter.onError(e);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        });
    }

    public Single<User> getUserRx() {
        return Single.create(emitter -> {
            try {

                User user = new User();
                SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
                String query = " SELECT * FROM " + User.TABLE_NAME;
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if (cursor.moveToNext()) {
                    user.setOldPassword(cursor.getString(cursor.getColumnIndex(User.COLUMN_OLD_PASSWORD)));
                    user.setNewPassword(cursor.getString(cursor.getColumnIndex(User.COLUMN_NEW_PASSWORD)));
                }
                cursor.close();
                sqLiteDatabase.close();
                emitter.onSuccess(user);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    private void deleteAllValues(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }

        for (String table : tables) {
            if (table.startsWith("sqlite_") || table.startsWith(User.TABLE_NAME)) {
                continue;
            }

            db.delete(table, null, null);
        }
        c.close();
    }

    public Single<Integer> insertIntoSchoolRx(School school) {

        return Single.create(emitter -> {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(School.COLUMN_SCHLNAME, school.getSchl_Name());
                values.put(School.COLUMN_SCHLADDRESS, school.getSchl_Addres());
                values.put(School.COLUMN_CONTACT, school.getSchl_Contact_Number());
                String countQuery = "SELECT  * FROM " + School.TABLE_NAME;
                Cursor cursor = db.rawQuery(countQuery, null);
                int count = cursor.getCount();
                cursor.close();
                db.close();
                emitter.onSuccess(count);
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }



    public Single<Integer> createTableForClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        return Single.create(emitter -> {
            try {
                List<String> classesList = new ArrayList<>();
                String selectQuery = " SELECT DISTINCT " + Student.COLUMN_CLASS_NAME + " FROM " + Student.TABLE_NAME;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        classesList.add(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
                    } while (cursor.moveToNext());
                }
                for (String className : classesList) {
                    if (CheckIfTableExists(className) == Utils.TABLE_DOESNOT_EXISTS) {
                        String CREATE_TABLE_NEW_CLASS = "CREATE TABLE " + className + "("
                                + Class.COLUMN_SLNO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                + Class.COLUMN_STUD_ID + " TEXT,"
                                + Class.COLUMN_CHECKIN_DATE + " TEXT ,"
                                + Class.COLUMN_CHECKOUT_DATE + " TEXT ,"
                                + Class.COLUMN_CHECKIN_TIME + " TEXT,"
                                + Class.COLUMN_CHECKOUT_TIME + " TEXT,"
                                + Class.COLUMN_CHECKIN_DATE_MILLI + " LONG "
                                + ")";
                        db.execSQL(CREATE_TABLE_NEW_CLASS);
                    }
                }
                cursor.close();
                db.close();
                emitter.onSuccess(Utils.SUCCESS);
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public void createTableClasses(String className) {
        String CREATE_TABLE_NEW_CLASS = "CREATE TABLE " + className + "("
                + Class.COLUMN_SLNO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Class.COLUMN_STUD_ID + " TEXT,"
                + Class.COLUMN_CHECKIN_DATE + " TEXT ,"
                + Class.COLUMN_CHECKOUT_DATE + " TEXT ,"
                + Class.COLUMN_CHECKIN_TIME + " TEXT,"
                + Class.COLUMN_CHECKOUT_TIME + " TEXT,"
                + Class.COLUMN_CHECKIN_DATE_MILLI + " LONG "
                + ")";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_NEW_CLASS);
        db.close();
    }



    public Single<List<String>> getAllClassesListFromStudentsRx() {
        return Single.create(emitter -> {
            try {
                List<String> classesList = new ArrayList<>();
                String selectQuery = " SELECT DISTINCT " + Student.COLUMN_CLASS_NAME + " FROM " + Student.TABLE_NAME;

                SQLiteDatabase db = this.getWritableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        classesList.add(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
                    } while (cursor.moveToNext());
                }
                db.close();
                cursor.close();
                emitter.onSuccess(classesList);
            } catch (Exception e) {
                e.printStackTrace();
                if (emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public Integer CheckIfTableExists(String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name =?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{tableName});
        if (cursor.getCount() > 0) {
            cursor.close();
            return Utils.TABLE_EXISTS;
        } else {
            cursor.close();
            return Utils.TABLE_DOESNOT_EXISTS;
        }

    }


    public List<String> getAllClassesListFromStudents(DataBaseListener listener) {
        listener.onStart();
        List<String> classesList = new ArrayList<>();
        String selectQuery = " SELECT DISTINCT " + Student.COLUMN_CLASS_NAME + " FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                classesList.add(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
            } while (cursor.moveToNext());
        }


        // close db connection
        db.close();
        cursor.close();
        listener.onComplete(Student.TABLE_NAME, classesList);
        return classesList;
    }



    public Single<Integer> changePasswordRx(User user) {
        return Single.create(emitter -> {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(User.COLUMN_OLD_PASSWORD, user.getOldPassword());
            contentValues.put(User.COLUMN_NEW_PASSWORD, user.getNewPassword());
            int result = sqLiteDatabase.update(User.TABLE_NAME, contentValues, User.COLUMN_OLD_PASSWORD + " =? ", new String[]{user.getOldPassword()});
            if (result != -1)
                emitter.onSuccess(result);
            else
                emitter.onError(new Throwable("Error occured while changing password,Please check your old password and try again"));
        });
    }


    public Single<Boolean> getStudentIsCheckedInRx(Class classPojo) {
        return Single.create(emitter -> {
            Boolean isCheckedIn = false;
            try {
                List<String> tableNames = new ArrayList<>();
                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
                Cursor studentCursor = sqLiteDatabase.query(Student.TABLE_NAME, new String[]{"*"}, Student.COLUMN_STUD_ID + "=?", new String[]{classPojo.getStudId()}, null, null, null);

                if (studentCursor.moveToNext()) {
                    do {
                        String className = studentCursor.getString(studentCursor.getColumnIndex(Student.COLUMN_CLASS_NAME));
                        tableNames.add(className);
                    } while (studentCursor.moveToNext());
                }
                if (studentCursor.getCount() > 0) {
                    studentCursor.close();
                    Cursor cursor = sqLiteDatabase.query(tableNames.get(0), new String[]{"*"}, Class.COLUMN_STUD_ID + "=? and " + Class.COLUMN_CHECKIN_DATE + "=?", new String[]{classPojo.getStudId(), classPojo.getCheckInDate()}, null, null, null);
                    if (cursor.getCount() == 0) {
                        isCheckedIn = false;
                        cursor.close();
                    } else if (cursor.getCount() > 0) {
                        cursor.close();
                        isCheckedIn = true;
                        // status = Utils.ALREADYEXISTS;
                    }

                } else {
                    studentCursor.close();
                }
                emitter.onSuccess(isCheckedIn);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    private List<String> getStudentClassesList(Class classPojo) {
        List<String> classesList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(Student.TABLE_NAME, new String[]{"*"}, Student.COLUMN_STUD_ID + " =? ", new String[]{classPojo.getStudId()}, null, null, null);

        while (cursor.moveToNext()) {
            classesList.add(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
        }
        cursor.close();
        return classesList;
    }

    public Single<Integer> setStudentSignedInRx(Class classPojo) {
        return Single.create(emitter -> {
            try {

                int status = Utils.NORESULT;
                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
                sqLiteDatabase.beginTransaction();
               List<String> classesList = getStudentClassesList(classPojo);

                if (classesList.size() > 0) {
                    for (String tableName : classesList) {
                        Cursor cursor = sqLiteDatabase.query(tableName, new String[]{Class.COLUMN_CHECKIN_TIME}, Class.COLUMN_STUD_ID + "=? and " + Class.COLUMN_CHECKIN_DATE + "=?", new String[]{classPojo.getStudId(), classPojo.getCheckInDate()}, null, null, null);
                        if (cursor.getCount() == 0) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Class.COLUMN_CHECKIN_DATE, classPojo.getCheckInDate());
                            contentValues.put(Class.COLUMN_CHECKIN_TIME, classPojo.getCheckInTime());
                            contentValues.put(Class.COLUMN_STUD_ID, classPojo.getStudId());
                            contentValues.put(Class.COLUMN_CHECKIN_DATE_MILLI, classPojo.getCheckInDateMilli());
                            sqLiteDatabase.insert(tableName, null, contentValues);
                            status = Utils.SUCCESS;
                            cursor.close();
                        } else if (cursor.getCount() > 0) {
                            cursor.moveToNext();
                            String checkInTime = cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_TIME));
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Class.COLUMN_CHECKIN_TIME, checkInTime + "," + classPojo.getCheckInTime());
                            sqLiteDatabase.update(tableName, contentValues, Class.COLUMN_STUD_ID + "=? and " + Class.COLUMN_CHECKIN_DATE + "=?", new String[]{classPojo.getStudId(), classPojo.getCheckInDate()});
                            status = Utils.SUCCESS;
                            cursor.close();
                        }
                    }
                }
                sqLiteDatabase.setTransactionSuccessful();
                sqLiteDatabase.endTransaction();
                sqLiteDatabase.close();
                emitter.onSuccess(status);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    public Single<Integer> setStudentCheckedOutRx(Class classPojo) {
        return Single.create(emitter -> {
            try {
                int status = Utils.NORESULT;
                List<String> tableNames = new ArrayList<>();
                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
                Cursor studentCursor = sqLiteDatabase.query(Student.TABLE_NAME, new String[]{"*"}, Student.COLUMN_STUD_ID + "=?", new String[]{classPojo.getStudId()}, null, null, null);
                if (studentCursor.moveToNext()) {
                    do {
                        String className = studentCursor.getString(studentCursor.getColumnIndex(Student.COLUMN_CLASS_NAME));
                        tableNames.add(className);
                    } while (studentCursor.moveToNext());
                }

                if (studentCursor.getCount() > 0) {
                    studentCursor.close();
                    for (String tableName : tableNames) {

                        Cursor checkInDateCursor = sqLiteDatabase.query(tableName, new String[]{"*"}, Class.COLUMN_STUD_ID + "=? and " + Class.COLUMN_CHECKIN_DATE + "=?", new String[]{classPojo.getStudId(), classPojo.getCheckOutDate()}, null, null, null);

                        if (checkInDateCursor.getCount() > 0) {
                            checkInDateCursor.close();
                            Cursor checkOutDateCursor = sqLiteDatabase.query(tableName, new String[]{"*"}, Class.COLUMN_STUD_ID + "=? and " + Class.COLUMN_CHECKOUT_DATE + "=?", new String[]{classPojo.getStudId(), classPojo.getCheckOutDate()}, null, null, null);
                            if (checkOutDateCursor.getCount() == 0) {
                                checkOutDateCursor.close();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Class.COLUMN_CHECKOUT_DATE, classPojo.getCheckOutDate());
                                contentValues.put(Class.COLUMN_CHECKOUT_TIME, classPojo.getCheckOutTime());
                                sqLiteDatabase.update(tableName, contentValues, Class.COLUMN_STUD_ID + "=? and " + Class.COLUMN_CHECKIN_DATE + " =? ", new String[]{classPojo.getStudId(), String.valueOf(classPojo.getCheckOutDate())});
                                status = Utils.SUCCESS;
                            } else status = Utils.ALREADYEXISTS;
                        } else {
                            checkInDateCursor.close();
                            status = Utils.NOCHECKINDATE;
                        }
                    }
                } else {
                    studentCursor.close();
                    status = Utils.NOSTUDENTID;
                }
                sqLiteDatabase.close();
                emitter.onSuccess(status);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }


    public Single<List<Class>> getStudentAttendanceListRx(String tableName, String studentId, long fromDate, long toDate, boolean isLast) {
        return Single.create(emitter -> {
            try {

                List<Class> list = new ArrayList<>();
                String query = " SELECT " + Class.COLUMN_CHECKIN_DATE + "," + Class.COLUMN_CHECKIN_TIME + "," + Class.COLUMN_CHECKOUT_TIME + " FROM " + tableName + " WHERE StudId = ? AND " + Class.COLUMN_CHECKIN_DATE_MILLI + " BETWEEN " + fromDate + " AND " + toDate + " ORDER by CheckInDateInMilliSec asc";
                SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{studentId});
                if (cursor.moveToNext()) {
                    do {
                        Class class_Pojo = new Class();
                        class_Pojo.setCheckInDate(Utils.convertDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_DATE))));
                        class_Pojo.setCheckOutTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_TIME)));
                        class_Pojo.setCheckInTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_TIME)));
                        list.add(class_Pojo);
                    } while (cursor.moveToNext());
                }
                if (isLast) {
                    cursor.close();
                    sqLiteDatabase.close();
                }
                emitter.onSuccess(list);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    public List<Class> getStudentAttendanceList(String tableName, String studentId, long fromDate, long toDate, boolean isLast) {
        List<Class> list = new ArrayList<>();
        String query = " SELECT " + Class.COLUMN_CHECKIN_DATE + "," + Class.COLUMN_CHECKIN_TIME + "," + Class.COLUMN_CHECKOUT_TIME + " FROM " + tableName + " WHERE StudId = ? AND " + Class.COLUMN_CHECKIN_DATE_MILLI + " BETWEEN " + fromDate + " AND " + toDate + " ORDER by CheckInDateInMilliSec asc";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{studentId});
        if (cursor.moveToNext()) {
            do {
                Class class_Pojo = new Class();
                class_Pojo.setCheckInDate(Utils.convertDate(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_DATE))));
                class_Pojo.setCheckOutTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKOUT_TIME)));
                class_Pojo.setCheckInTime(cursor.getString(cursor.getColumnIndex(Class.COLUMN_CHECKIN_TIME)));
                list.add(class_Pojo);
            } while (cursor.moveToNext());
        }
        if (isLast) {
            cursor.close();
            sqLiteDatabase.close();
        }
        return list;
    }


    public List<Student> getstudentListWhoHasAttendance(String tableName) {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = " SELECT " + Student.COLUMN_STUD_ID + "," + Student.COLUMN_STUD_NAME + "," + Student.COLUMN_FATHER_NAME + "," + Student.COLUMN_CLASS_NAME + " FROM " + Student.TABLE_NAME + " JOIN " + tableName + " WHERE " + Student.COLUMN_STUD_ID + " = " + Class.COLUMN_STUD_ID;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToNext()) {
            do {
                Student student = new Student();
                student.setStud_Id(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_ID)));
                student.setStud_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_NAME)));
                student.setFather_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_FATHER_NAME)));
                student.setClass_Name(cursor.getString(cursor.getColumnIndex(Student.COLUMN_CLASS_NAME)));
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return studentList;
    }

    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + User.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int getSchoolsCount() {
        String countQuery = "SELECT  * FROM " + School.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public Single<SendMessagePojo> getCheckedInMessageRx(String studId, String inTime) {
        return Single.create(emitter -> {
            try {
                String studName = "", phoneNumber = "";
                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.query(Student.TABLE_NAME, new String[]{"*"}, Student.COLUMN_STUD_ID + " =? ", new String[]{studId}, null, null, null);
                if (cursor.moveToNext()) {
                    studName = cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_NAME));
                    phoneNumber = cursor.getString(cursor.getColumnIndex(Student.COLUMN_PHONE));
                }
                SendMessagePojo sendMessagePojo = new SendMessagePojo();
                sendMessagePojo.setMessage(Utils.getCheckInOutMessage(studName, inTime, Preferences.SCHOOL_NAME));
                sendMessagePojo.setPhoneNumber(phoneNumber);
                cursor.close();
                sqLiteDatabase.close();
                emitter.onSuccess(sendMessagePojo);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    public SendMessagePojo getCheckOutMessage(String studId, String inTime) {
        String studName = "", phoneNumber = "";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(Student.TABLE_NAME, new String[]{"*"}, Student.COLUMN_STUD_ID + " =? ", new String[]{studId}, null, null, null);
        if (cursor.moveToNext()) {
            studName = cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUD_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(Student.COLUMN_PHONE));
        }

        SendMessagePojo sendMessagePojo = new SendMessagePojo();
        sendMessagePojo.setMessage(Utils.getCheckInOutMessage(studName, inTime, Preferences.SCHOOL_NAME));
        sendMessagePojo.setPhoneNumber(phoneNumber);
        cursor.close();
        sqLiteDatabase.close();
        return sendMessagePojo;

    }


}