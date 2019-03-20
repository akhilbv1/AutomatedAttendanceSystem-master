package com.projects.automatedattendancesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.projects.automatedattendancesystem.Pojo.MessagePojo;
import com.projects.automatedattendancesystem.Pojo.SendMessagePojo;
import com.projects.automatedattendancesystem.Sqlite.SqliteHelper;
import com.projects.automatedattendancesystem.Tables.Class;
import com.projects.automatedattendancesystem.Tables.Student;
import com.projects.automatedattendancesystem.Tables.StudentAttendanceStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class UploadAttendanceFragment extends Fragment implements View.OnClickListener, DataBaseListener {

    private EditText etStudentId;

    private SqliteHelper sqliteHelper;

    private List<String> classList;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_upload_attendance, container, false);
        initialiseViews(view);
        Preferences.loadPreferences(getActivity());
        sqliteHelper = new SqliteHelper(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //classList = sqliteHelper.getAllClassesListFromStudents(this);
        getClassesList();
        //getDateMilli();
        return view;
    }

    private void initialiseViews(View view) {
        //  Button btnCheckIn = view.findViewById(R.id.btnCheckin);
        // Button btnCheckout = view.findViewById(R.id.btnCheckout);
        etStudentId = view.findViewById(R.id.etStudentId);

        // btnCheckIn.setOnClickListener(this);
        // btnCheckout.setOnClickListener(this);

        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        Button btnSendMessage = view.findViewById(R.id.btnSend);
        btnSendMessage.setOnClickListener(this);

        Button btnMultiple = view.findViewById(R.id.btnMultiple);
        btnMultiple.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MultiChecksActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCheckin:
                checkIn();
                break;

            case R.id.btnCheckout:
                checkOut();
                break;

            case R.id.btnSend:
                // sendAbsentsAlert();
                getAbsentStudents();
                break;

            case R.id.btnSubmit:
                //setStudentAttendance();
                checkIn();
                break;
        }
    }

    private void getClassesList() {
        mProgressDialog.show();
        if (sqliteHelper.CheckIfTableExists(Student.TABLE_NAME) == Utils.TABLE_EXISTS) {
            sqliteHelper.getAllClassesListFromStudentsRx().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(List<String> strings) {
                            mProgressDialog.dismiss();
                            if (strings.size() > 0)
                                classList = strings;
                            else
                                Toast.makeText(getActivity(), "Please upload data from settings", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            mProgressDialog.dismiss();
        }
    }

    private void setStudentAttendance() {
        if (!TextUtils.isEmpty(etStudentId.getText().toString().trim())) {
            if (classList != null && classList.size() > 0) {
                mProgressDialog.show();
                long currentTime = System.currentTimeMillis();
                Class classPojo = new Class();
                classPojo.setStudId(etStudentId.getText().toString().trim());
                classPojo.setCheckInTime(getTime(currentTime));
                classPojo.setCheckInDate(getDate(currentTime));
                classPojo.setCheckInDateMilli(currentTime);

                sqliteHelper.getStudentIsCheckedInRx(classPojo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Boolean isCheckedIn) {
                                if (isCheckedIn) {
                                    checkOut();
                                } else {
                                    checkIn();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void checkIn() {

        if (!TextUtils.isEmpty(etStudentId.getText().toString().trim())) {
            long currentTime = System.currentTimeMillis();
            Class classPojo = new Class();
            classPojo.setStudId(etStudentId.getText().toString().trim());
            classPojo.setCheckInTime(getTime(currentTime));
            classPojo.setCheckInDate(getDate(currentTime));
            classPojo.setCheckInDateMilli(currentTime);
            //int status = sqliteHelper.setStudentCheckedIn(classList, classPojo);

            if (classList != null && classList.size() > 0) {
                mProgressDialog.show();

                sqliteHelper.setStudentSignedInRx(classPojo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                mProgressDialog.dismiss();
                                if (integer == Utils.SUCCESS) {
                                      getcheckInMessage(currentTime);
                                    etStudentId.getText().clear();
                                    showStatus(integer, "CheckedIn Successfully");
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        });
/*
                sqliteHelper.setStudentCheckedInRx(classPojo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                if (integer == Utils.SUCCESS) {
                                    getcheckInMessage(currentTime);
                                    //SendMessagePojo sendMessagePojo = sqliteHelper.getCheckInMessage(etStudentId.getText().toString().trim(), getTimeInHrs(currentTime));
                                    // sendMessage(sendMessagePojo.getMessage(), sendMessagePojo.getPhoneNumber());
                                    //sendCheckInOutMessage(sendMessagePojo.getMessage(), sendMessagePojo.getPhoneNumber());
                                }
                                etStudentId.getText().clear();
                                mProgressDialog.dismiss();
                                showStatus(integer, "CheckedIn Successfully");
                            }

                            @Override
                            public void onError(Throwable e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        });*/


            } else {
                Toast.makeText(getActivity(), "Please enter student Id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No classes found", Toast.LENGTH_SHORT).show();
        }
    }

    private void getcheckInMessage(long currentTime) {
        sqliteHelper.getCheckedInMessageRx(etStudentId.getText().toString().trim(), getTimeInHrs(currentTime)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<SendMessagePojo>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(SendMessagePojo sendMessagePojo) {
                sendMessage(sendMessagePojo.getMessage(), sendMessagePojo.getPhoneNumber());

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm", cal).toString();
        return date;
    }

    private String getTimeInHrs(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm a", cal).toString();
        return date;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private void getDateMilli() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("MM", Locale.US);
        SimpleDateFormat simpleDateFormatYear = new SimpleDateFormat("yyyy", Locale.US);
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("dd", Locale.US);
        String month = simpleDateFormatMonth.format(date);
        String year = simpleDateFormatYear.format(date);
        String day = simpleDateFormatDay.format(date);

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.set(Calendar.MONTH, Integer.parseInt(month));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        calendar.set(Calendar.YEAR, Integer.parseInt(year));

        Log.i("time", "" + calendar.getTime());
    }

    private void checkOut() {
        if (!TextUtils.isEmpty(etStudentId.getText().toString().trim())) {

            long currentTime = System.currentTimeMillis();
            Class classPojo = new Class();
            classPojo.setStudId(etStudentId.getText().toString().trim());
            classPojo.setCheckOutTime(getTime(currentTime));
            classPojo.setCheckOutDate(getDate(currentTime));
            //   int status = sqliteHelper.setStudentCheckedOut(classList, classPojo);
            if (classList != null && classList.size() > 0) {
                mProgressDialog.show();
                sqliteHelper.setStudentCheckedOutRx(classPojo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                if (integer == Utils.SUCCESS) {
                                    SendMessagePojo sendMessagePojo = sqliteHelper.getCheckOutMessage(etStudentId.getText().toString().trim(), getTimeInHrs(currentTime), false);
                                    sendMessage(sendMessagePojo.getMessage(), sendMessagePojo.getPhoneNumber());
                                    //sendCheckInOutMessage(sendMessagePojo.getMessage(), sendMessagePojo.getPhoneNumber());
                                }
                                mProgressDialog.dismiss();
                                showStatus(integer, "checkOut Successfully");
                                etStudentId.getText().clear();
                            }

                            @Override
                            public void onError(Throwable e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(getActivity(), "Please enter student Id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No classes found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStatus(int status, String message) {
        switch (status) {
            case Utils.SUCCESS: {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
            }
            case Utils.ALREADYEXISTS: {
                Toast.makeText(getActivity(), "Student Id already exists", Toast.LENGTH_SHORT).show();
                break;
            }
            case Utils.FAILURE: {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
            }
            case Utils.NOCHECKINDATE: {
                Toast.makeText(getActivity(), "Please checkIn first", Toast.LENGTH_SHORT).show();
                break;
            }
            case Utils.NORESULT: {
                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                break;
            }
            case Utils.NOSTUDENTID: {
                Toast.makeText(getActivity(), "Please enter valid Student Id", Toast.LENGTH_SHORT).show();
                break;
            }

        }

    }

    @Override
    public void onComplete(String tableName, Object result) {
        if (tableName.equals(Student.TABLE_NAME) && result != null) {
            if (result instanceof List) {
                List<String> classesList = (List<String>) result;

            }
        }
    }

    private void getAbsentStudents() {
        mProgressDialog.show();
        List<MessagePojo> messagePojoList = new ArrayList<>();
        if (sqliteHelper.CheckIfTableExists(StudentAttendanceStatus.TABLE_NAME) == Utils.TABLE_EXISTS) {
            sqliteHelper.getAbsentStudents().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<StudentAttendanceStatus>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(List<StudentAttendanceStatus> studentAttendanceStatuses) {
                            mProgressDialog.dismiss();
                            if (studentAttendanceStatuses.size() > 0) {
                                for (StudentAttendanceStatus obj : studentAttendanceStatuses) {
                                    MessagePojo messagePojo = new MessagePojo();
                                    messagePojo.setMessage(Utils.getMessage(obj.getStudName(), Preferences.SCHOOL_NAME));
                                    messagePojo.setMobileNum(obj.getStud_Number());
                                    messagePojoList.add(messagePojo);
                                }
                                //    sendAbsentsAlert(messagePojoList);
                            } else {
                                Toast.makeText(getActivity(), "No Student's to send message", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendMessage(String message, String phoneNumber) {
        mProgressDialog.show();
        RestClient restClient = new RestClient();
        Single<Response<Void>> sendMessage = restClient.getRestApi().postMessage("akredmi2017", "123456", "DBSSAS", phoneNumber, message);
        sendMessage.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Response<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(Response<Void> voidResponse) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Message has been sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                mProgressDialog.dismiss();
                e.printStackTrace();
            }
        });
    }


    private void sendCheckInOutMessage(String message, String phoneNumber) {
        mProgressDialog.show();
        RestClient restClient = new RestClient();
        Single<Error> sendMessage = restClient.getRestApi().sendMessage(Utils.SMS_API_KEY, message, "TXTLCL", phoneNumber);
        sendMessage.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Error>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(Error error) {
                mProgressDialog.dismiss();
                if (error.getStatus().equalsIgnoreCase("failure")) {
                    Toast.makeText(getActivity(), error.getErrorResponse().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                mProgressDialog.dismiss();
                e.printStackTrace();
            }
        });
    }

   /* private void sendAbsentsAlert(List<MessagePojo> messagesList) {
        mProgressDialog.show();
        RestClient restClient = new RestClient();

        *//*MessagePojo messagePojo = new MessagePojo("Testing BUlk Sms", "9494312360");
        MessagePojo messagePojo1 = new MessagePojo("Testing BUlk Sms", "8341770556");

        List<MessagePojo> messagesList1 = new ArrayList<>();
         messagesList.add(messagePojo);
        messagesList1.add(messagePojo1);*//*

        MultipleSmsPojo multipleSmsPojo = new MultipleSmsPojo();
        multipleSmsPojo.setSenderName("TXTLCL");
        multipleSmsPojo.setMessagePojoList(messagesList);


        Gson gson = new Gson();
        String json = gson.toJson(multipleSmsPojo);

        Single<Error> sendMessage = restClient.getRestApi().postMessage(Utils.SMS_API_KEY, json);

        sendMessage.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Error>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Error error) {
                        mProgressDialog.dismiss();
                        if (error.getStatus().equalsIgnoreCase("failure")) {
                          *//*  Log.i("code", "" + error.getStatus());
                            Log.i("code", "" + error.getErrorResponse().get(0).getCode());
                            Log.i("code", "" + error.getErrorResponse().get(0).getMessage());*//*
                            Toast.makeText(getActivity(), error.getErrorResponse().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            // Log.i("code", "Success");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        e.printStackTrace();
                    }
                });

        *//*Single<Void> sendMessage = restClient.getRestApi().sendMessage(Utils.SMS_API_KEY, "TEsting Retrofit", "TXTLCL", "918341770556");

        sendMessage.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Void aVoid) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });*//*

    }*/

   /* private static class SendSmsAsyntask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String result = SendSMS.sendSms();
            Log.i("result", result);
            return null;
        }
    }*/
}
