package com.projects.automatedattendancesystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.automatedattendancesystem.Sqlite.SqliteHelper;
import com.projects.automatedattendancesystem.Tables.School;
import com.projects.automatedattendancesystem.Tables.Student;
import com.projects.automatedattendancesystem.Tables.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SettingsFragment extends Fragment implements View.OnClickListener, SQLiteTransactionListener, DataBaseListener {

    private TextInputEditText etSchlName, etSchlAddress, etSchlContactNum, etOldPassword, etNewPassword, etConfirmPassword;

    private TextView tvFileName;

    private SqliteHelper sqliteHelper;

    private List<Student> list;

    private ProgressDialog mProgressDialog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private AlertDialog mAlertDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialiseViews(view);
        sqliteHelper = new SqliteHelper(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        passwordDialog();
        return view;
    }

    private void initialiseViews(View view) {
        etSchlName = view.findViewById(R.id.etSchlName);
        etSchlAddress = view.findViewById(R.id.etSchlAddress);
        etSchlContactNum = view.findViewById(R.id.etContactNumber);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        Button btnSaveSchl = view.findViewById(R.id.btnSaveSchl);
        Button btnSavePassword = view.findViewById(R.id.btnSavePassword);
        Button btnSaveXls = view.findViewById(R.id.btnSaveXls);
        Button btnBrowseXls = view.findViewById(R.id.btnBrowseXls);
        tvFileName = view.findViewById(R.id.tvFileName);

        btnBrowseXls.setOnClickListener(this);
        btnSavePassword.setOnClickListener(this);
        btnSaveSchl.setOnClickListener(this);
        btnSaveXls.setOnClickListener(this);

    }



    private void passwordDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(view1 -> {
            sqliteHelper.getUserRx().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(User user) {
                            if (!TextUtils.isEmpty(etPassword.getText().toString().trim())) {
                                if (etPassword.getText().toString().trim().equalsIgnoreCase(user.getNewPassword())) {
                                    mAlertDialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Please enter valid password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        mAlertDialog = builder.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();
    }


    private void validateAndSaveSchoolDetails() {

        if (TextUtils.isEmpty(etSchlName.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter School Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etSchlAddress.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter School Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etSchlContactNum.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter School Contact Number", Toast.LENGTH_SHORT).show();
        } else {
            saveSchoolDetailsIntoDb();
        }
    }

    private void validateAndChangePassword() {

        if (TextUtils.isEmpty(etOldPassword.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter Old Password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etNewPassword.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter New Password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etConfirmPassword.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter New Password", Toast.LENGTH_SHORT).show();
        } else if (etOldPassword.getText().toString().trim().equals(etNewPassword.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Old and New Passwords Cannot be the same", Toast.LENGTH_SHORT).show();
        } else if (!etNewPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Passwords Do not Match", Toast.LENGTH_SHORT).show();
        } else {
            changePasswordInDb();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        startActivityForResult(intent, 189);
    }

    private void browseDocuments() {
        String[] mimeTypes =
                {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 189);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 189 && data != null) {
            Uri uri = data.getData();
            mProgressDialog.show();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                String fileName = getFileName(uri);
                tvFileName.setText(fileName);
                ReadExcelData.intialiseSingle(inputStream, fileName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<Student>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(List<Student> studentList) {
                                mProgressDialog.dismiss();
                                list = studentList;
                            }

                            @Override
                            public void onError(Throwable e) {
                                mProgressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Error while parsing the document", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (FileNotFoundException e) {
                mProgressDialog.dismiss();

                e.printStackTrace();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void changePasswordInDb() {
        mProgressDialog.show();
        int oldCount = sqliteHelper.getUserCount();
        User user = new User();
        user.setOldPassword(etOldPassword.getText().toString().trim());
        user.setNewPassword(etNewPassword.getText().toString().trim());
        sqliteHelper.changePasswordRx(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        mProgressDialog.dismiss();
                        etOldPassword.getText().clear();
                        etNewPassword.getText().clear();
                        etConfirmPassword.getText().clear();
                        if (integer == 1) {
                            Toast.makeText(getActivity(), "Password has been changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Password change failed,Please try again", Toast.LENGTH_SHORT).show();
                        }
                        /*if (integer > oldCount) {
                            Toast.makeText(getActivity(), "Password has been changed successfully", Toast.LENGTH_SHORT).show();
                        }*/

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveSchoolDetailsIntoDb() {
        if (sqliteHelper.CheckIfTableExists(School.TABLE_NAME) == Utils.TABLE_EXISTS) {
            mProgressDialog.show();
            int oldCount = sqliteHelper.getSchoolsCount();
            School school = new School();
            school.setSchl_Name(etSchlName.getText().toString().trim());
            school.setSchl_Addres(etSchlAddress.getText().toString().trim());
            school.setSchl_Contact_Number(etSchlContactNum.getText().toString().trim());
            sqliteHelper.insertIntoSchoolRx(school).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            mProgressDialog.dismiss();
                            int newCount = integer;
                            if (newCount > oldCount) {
                                Preferences.SCHOOL_NAME = etSchlName.getText().toString().trim();
                                Preferences.savePreferences(getActivity());
                                Preferences.loadPreferences(getActivity());
                                TextView tvCollegeName = getActivity().findViewById(R.id.tvCollegename);
                                tvCollegeName.setText(Preferences.SCHOOL_NAME);
                                etSchlName.getText().clear();
                                etSchlAddress.getText().clear();
                                etSchlContactNum.getText().clear();
                                Toast.makeText(getActivity(), "School details have been saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                etSchlName.getText().clear();
                                etSchlAddress.getText().clear();
                                etSchlContactNum.getText().clear();
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void saveStudentDetailInDb() {
        if (list!=null && list.size() > 0) {
            mProgressDialog.show();
            if (sqliteHelper.CheckIfTableExists(Student.TABLE_NAME) == Utils.TABLE_EXISTS) {
                sqliteHelper.getStudentDetailsObserver(list).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                mProgressDialog.dismiss();
                                createTablesForClassesRx();
                                if (integer == Utils.SUCCESS) {
                                    Toast.makeText(getActivity(), "Document has been uploaded Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Please choose the document to save", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBrowseXls:
                browseDocuments();
                break;

            case R.id.btnSavePassword:
                validateAndChangePassword();
                break;

            case R.id.btnSaveSchl:
                validateAndSaveSchoolDetails();
                break;

            case R.id.btnSaveXls:
                saveStudentDetailInDb();

                break;
        }
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onCommit() {
        Toast.makeText(getActivity(), "Data has been uploaded successfully", Toast.LENGTH_SHORT).show();
        Log.i("count", "" + sqliteHelper.getStudentsCount());
        tvFileName.setText("");
    }


    @Override
    public void onRollback() {

    }

    private void createTablesForClassesRx() {
        mProgressDialog.show();

        sqliteHelper.createTableForClasses().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(Integer integer) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.Error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTablesForClasses(List<String> classesList) {
        for (String className : classesList) {
            sqliteHelper.createTableClasses(className);
        }
    }

    @Override
    public void onComplete(String tableName, Object result) {
        if (tableName.equals(Student.TABLE_NAME) && result != null) {
            if (result instanceof List)
                createTablesForClasses((List<String>) result);
        } else if (tableName.equals(Student.TABLE_NAME) && result == null) {
            sqliteHelper.getAllClassesListFromStudents(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}