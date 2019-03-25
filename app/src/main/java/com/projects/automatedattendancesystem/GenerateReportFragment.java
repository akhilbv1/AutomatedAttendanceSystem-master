package com.projects.automatedattendancesystem;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.automatedattendancesystem.Pojo.ReportPojo;
import com.projects.automatedattendancesystem.Sqlite.SqliteHelper;
import com.projects.automatedattendancesystem.Tables.Class;
import com.projects.automatedattendancesystem.Tables.Student;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class GenerateReportFragment extends Fragment implements AdapterView.OnItemSelectedListener, DataBaseListener, AsyncListener {

    private static final int PERMISSSIONS_REQ_CODE = 125;
    List<Student> studentList;
    List<String> datesList;
    long fromDateInSec = 0;
    long toDateInSec = 0;
    private Spinner spClass;
    private String className;
    private List<String> classesList = new ArrayList<>();
    private EditText etEmail, etFromDate, etToDate;
    private SqliteHelper sqliteHelper;
    private Button btnGenerate;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressDialog mProgressDialog;
    private String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private TextView tvFileName;
    private MaterialCardView cvFile;
    private File filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_class_repor, container, false);
        initialiseViews(view);
        sqliteHelper = new SqliteHelper(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        //askPermissions();
        Utils.checkAndRequestPermissions(getActivity(), PERMISSSIONS_REQ_CODE);
        getClassesList();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }



    private void getClassesList() {
        if (sqliteHelper.CheckIfTableExists(Student.TABLE_NAME) == Utils.TABLE_EXISTS) {
            classesList = sqliteHelper.getAllClassesListFromStudents(this);
            initializeSpinner(classesList);
        }
    }

    private void initialiseViews(View view) {
        spClass = view.findViewById(R.id.spClass);
        etEmail = view.findViewById(R.id.etEmail);

        etFromDate = view.findViewById(R.id.etFromDate);
        etFromDate.setOnClickListener(this::FromdatePickerDialog);

        etToDate = view.findViewById(R.id.etToDate);
        etToDate.setOnClickListener(this::ToDatePickerDialog);

        btnGenerate = view.findViewById(R.id.btnGenerateReport);
        btnGenerate.setOnClickListener(this::generateReport);

        Button btnSendEmail = view.findViewById(R.id.btnSend);
        btnSendEmail.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(etEmail.getText().toString().trim()))
                Toast.makeText(getActivity(), "Please enter email address", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(etEmail.getText().toString().trim()) && Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches())
                Toast.makeText(getActivity(), "Please enter valid email address", Toast.LENGTH_SHORT).show();
            else
                sendMail(etEmail.getText().toString().trim());
        });

        cvFile = view.findViewById(R.id.cvFileName);
        tvFileName = view.findViewById(R.id.tvFileName);
        cvFile.setVisibility(View.GONE);
    }

    @Override
    public void onComplete() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onShowMessage(String message) {
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void doVisibilityOperations(int visibility) {

    }

    @Override
    public void onCompleteWithResult(Object result) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (result instanceof String) {
            String fileName = (String) result;
            filePath = new File(Environment.getExternalStorageDirectory() + "/AutomatedAttendanceReport/" + fileName);

            setFileName(fileName, filePath);
        }
    }

    @Override
    public void showToast(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    private ProgressDialog showProgressBar() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Students List from database");
        progressDialog.show();
        return progressDialog;
    }

    private void dismissProgressBar() {
        mProgressDialog.dismiss();
    }

    public void generateReport(View v) {
        if (TextUtils.isEmpty(etFromDate.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter from date.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etToDate.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter to date", Toast.LENGTH_SHORT).show();
        } else {
            new GenerateReportAsyntask(this, sqliteHelper, etFromDate.getText().toString(), etToDate.getText().toString(), className).execute();
          /*  List<ReportPojo> reports = new ArrayList<>();


            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date fromDate = dateFormat.parse(etFromDate.getText().toString().trim());
                Date ToDate = dateFormat.parse(etToDate.getText().toString().trim());
                fromDateInSec = fromDate.getTime();
                toDateInSec = ToDate.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sqliteHelper.CheckIfTableExists(className) == Utils.TABLE_EXISTS) {
                sqliteHelper.getstudentListWhoHasAttendanceRx(className).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<Student>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(List<Student> students) {
                                mProgressDialog.dismiss();
                                studentList = students;
                                if (studentList.size() > 0)
                                    generateReportsList(studentList, reports, fromDateInSec, toDateInSec);
                                else
                                    Toast.makeText(getActivity(), "No Students found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }*/
        }
        //  reportPojo.setAttendanceList(attendanceList);
        //  reports.add(reportPojo);
    }

    private void generateReportsList(List<Student> studentList, List<ReportPojo> reports, long fromDateInSec, long toDateInSec) {
        mProgressDialog.setMessage("Fetching Report from Database");
        mProgressDialog.show();

        if (studentList.size() > 0) {
            for (int i = 0; i < studentList.size(); i++) {
                ReportPojo reportPojo = new ReportPojo();
                boolean isLast = i == studentList.size() - 1;
                Student studentPojo = studentList.get(i);
                reportPojo.setSlNo(String.valueOf(i));
                reportPojo.setStud_Id(studentPojo.getStud_Id());
                reportPojo.setStud_Name(studentPojo.getStud_Name());
                reportPojo.setFather_Name(studentPojo.getFather_Name());
                reportPojo.setClass_Name(studentPojo.getClass_Name());
                // List<Class> attendanceList = sqliteHelper.getStudentAttendanceList(className, studentPojo.getStud_Id(), fromDateInSec, toDateInSec, isLast);
                if (sqliteHelper.CheckIfTableExists(className) == Utils.TABLE_EXISTS) {
                    sqliteHelper.getStudentAttendanceListRx(className, studentPojo.getStud_Id(), fromDateInSec, toDateInSec, isLast).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<Class>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    compositeDisposable.add(d);
                                }

                                @Override
                                public void onSuccess(List<Class> classes) {
                                    mProgressDialog.dismiss();
                                    reportPojo.setAttendanceList(classes);
                                    reports.add(reportPojo);
                                    datesList = Utils.getDates(etFromDate.getText().toString().trim(), etToDate.getText().toString().trim());
                                    generateReport(reports, datesList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    mProgressDialog.dismiss();
                    List<Class> classes = new ArrayList<>();
                    reportPojo.setAttendanceList(classes);
                    reports.add(reportPojo);
                    datesList = Utils.getDates(etFromDate.getText().toString().trim(), etToDate.getText().toString().trim());
                    generateReport(reports, datesList);
                }

            }
        } else {
            Toast.makeText(getActivity(), "No Students Found", Toast.LENGTH_SHORT).show();
        }


    }

    private void generateReport(List<ReportPojo> reports, List<String> datesList) {
        mProgressDialog.setMessage("Writing Report to Excel");
        mProgressDialog.show();
        long time_stamp = System.currentTimeMillis();
        String fileName = "Report_" + time_stamp + ".xlsx";
        File filePath = new File(Environment.getExternalStorageDirectory() + "/AutomatedAttendanceReport/" + fileName);

        setFileName("FileName:-" + fileName, filePath);
        //  int status = WriteExcelData.generateExcelReport("Report_" + time_stamp + ".xlsx", reports, datesList);
        WriteExcelData.generateExcelReportRx(fileName, reports, datesList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        mProgressDialog.dismiss();
                        if (integer == Utils.SUCCESS)
                            Toast.makeText(getActivity(), "Report Generated successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Report Generation Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setFileName(String fileName, File file) {
        cvFile.setVisibility(View.VISIBLE);
        String totName = "File Name:-" + fileName;
        SpannableString spannableString = new SpannableString(totName);

        spannableString.setSpan(new UnderlineSpan(), 11, totName.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 11, totName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvFileName.setText(spannableString, TextView.BufferType.SPANNABLE);
        tvFileName.setMovementMethod(LinkMovementMethod.getInstance());
        tvFileName.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(getActivity().getApplicationContext(), getString(R.string.file_provider_authority), file), "application/vnd.ms-excel");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

    }

    private String getDate(String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date dbDate = simpleDateFormat.parse(time);
            String date = DateFormat.format("dd MMM", dbDate).toString();
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void FromdatePickerDialog(View v) {
        Calendar newCalendar = Calendar.getInstance();

        int newYear;
        int newMonth;
        int newDay;
        newYear = newCalendar.get(Calendar.YEAR);
        newMonth = newCalendar.get(Calendar.MONTH);
        newDay = newCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog;
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        datePickerDialog = new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            etFromDate.setText(dateFormatter.format(newDate.getTime()));
        }, newYear, newMonth, newDay);

        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    public void ToDatePickerDialog(View v) {
        Calendar newCalendar = Calendar.getInstance();

        int newYear;
        int newMonth;
        int newDay;
        newYear = newCalendar.get(Calendar.YEAR);
        newMonth = newCalendar.get(Calendar.MONTH);
        newDay = newCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog;
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        datePickerDialog = new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            etToDate.setText(dateFormatter.format(newDate.getTime()));
        }, newYear, newMonth, newDay);

        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    //initialise spinner with static list
    private void initializeSpinner(List<String> classesList) {
        spClass.setVisibility(View.VISIBLE);
        if (classesList.size() > 6) {
            Field popup;
            try {
                popup = Spinner.class.getDeclaredField("mPopup");

                popup.setAccessible(true);

                // Get private mPopup member variable and try cast to ListPopupWindow
                android.widget.ListPopupWindow popupWindow;

                popupWindow = (android.widget.ListPopupWindow) popup.get(spClass);

                popupWindow.setHeight(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_custom_text, classesList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spClass.setAdapter(arrayAdapter);
        spClass.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        className = classesList.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        className = classesList.get(0);
    }

    @Override
    public void onComplete(String tableName, Object result) {

    }

    //check required permission and mail to user
    private void sendMail(String email) {
        if (!TextUtils.isEmpty(tvFileName.getText().toString().trim())) {
            //Implicit email intent
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            //  intent.setType("text/plain");
            // Uri path = Uri.fromFile(filePath);
            Uri fileUri = FileProvider.getUriForFile(getActivity(), getString(R.string.file_provider_authority), filePath);
            emailIntent.setType("*/*");
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendance Report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached Attendance report");
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

            startActivity(Intent.createChooser(emailIntent, "Send Email via"));
        }
    }


    private static class GenerateReportAsyntask extends AsyncTask<Void, Void, Integer> {

        List<ReportPojo> reports = new ArrayList<>();
        List<Student> studentList;
        List<String> datesList;
        private SqliteHelper sqliteHelper;
        private String fromDateString;
        private String toDate;
        private long fromDateInSec, toDateInSec;
        private String className;
        private ProgressDialog progressDialog;

        private AsyncListener listener;

        private String fileName;

        public GenerateReportAsyntask(AsyncListener listener, SqliteHelper sqliteHelper, String fromDate, String toDate, String className) {
            this.listener = listener;
            this.sqliteHelper = sqliteHelper;
            this.fromDateString = fromDate;
            this.toDate = toDate;
            this.className = className;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog =  new GenerateReportFragment().showProgressBar();
            listener.onShowMessage("Loading");
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            List<ReportPojo> reports = new ArrayList<>();
            Integer status = Utils.NORESULT;

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date fromDate = dateFormat.parse(fromDateString);
                Date ToDate = dateFormat.parse(toDate);
                fromDateInSec = fromDate.getTime();
                toDateInSec = ToDate.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sqliteHelper.CheckIfTableExists(className) == Utils.TABLE_EXISTS) {
                studentList = sqliteHelper.getstudentListWhoHasAttendance(className);

                if (studentList.size() > 0) {
                    for (int i = 0; i < studentList.size(); i++) {
                        ReportPojo reportPojo = new ReportPojo();
                        boolean isLast = i == studentList.size() - 1;
                        Student studentPojo = studentList.get(i);
                        reportPojo.setSlNo(String.valueOf(i));
                        reportPojo.setStud_Id(studentPojo.getStud_Id());
                        reportPojo.setStud_Name(studentPojo.getStud_Name());
                        reportPojo.setFather_Name(studentPojo.getFather_Name());
                        reportPojo.setClass_Name(studentPojo.getClass_Name());
                        if (sqliteHelper.CheckIfTableExists(className) == Utils.TABLE_EXISTS) {
                            reportPojo.setAttendanceList(sqliteHelper.getStudentAttendanceList(className, studentPojo.getStud_Id(), fromDateInSec, toDateInSec, isLast));
                            reports.add(reportPojo);
                            datesList = Utils.getDates(fromDateString, toDate);
                            status = generateReport(reports, datesList);

                        } else {
                            List<Class> classes = new ArrayList<>();
                            reportPojo.setAttendanceList(classes);
                            reports.add(reportPojo);
                            datesList = Utils.getDates(fromDateString, toDate);
                            status = generateReport(reports, datesList);
                        }
                    }
                } else {
                    status = Utils.NOSTUDENTSEXIST;
                }

            }
            return status;
        }

        private int generateReport(List<ReportPojo> reports, List<String> datesList) {
            long time_stamp = System.currentTimeMillis();
            fileName = "Report_" + time_stamp + ".xlsx";
            return WriteExcelData.generateExcelReport(fileName, reports, datesList);
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //progressDialog.dismiss();

            //new GenerateReportFragment().dismissProgressBar();
            String message = "Please try again";
            if (result == Utils.SUCCESS)
                message = "Report Generated Successfully";
            else if (result == Utils.FAILURE)
                message = "Report Generation failed,Please try again";
            else if (result == Utils.NOSTUDENTSEXIST)
                message = "No Students Found";

            listener.showToast(message);
            listener.onCompleteWithResult(fileName);


            //   new GenerateReportFragment().showToast(message);

        }
    }

}
