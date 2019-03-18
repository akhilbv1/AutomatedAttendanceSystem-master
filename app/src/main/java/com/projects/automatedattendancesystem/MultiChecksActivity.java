package com.projects.automatedattendancesystem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.projects.automatedattendancesystem.Pojo.TagsStatePojo;
import com.projects.automatedattendancesystem.Sqlite.SqliteHelper;
import com.projects.automatedattendancesystem.Tables.Class;
import com.projects.automatedattendancesystem.Tables.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MultiChecksActivity extends AppCompatActivity implements MultiTagsSelectAdapter.OnMultiTagsSelectedListener, MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener, View.OnClickListener {

    private RecyclerView rvMultipleChecks;

    private List<String> multiTagsList = new ArrayList<>();

    private MultiTagsSelectAdapter multiTagsSelectAdapter;

    private MaterialSearchView materialSearchView;

    private List<TagsStatePojo> studentsIdsList = new ArrayList<>();

    private SqliteHelper sqliteHelper;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<String> classList;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_list);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        initialiseViews();
        sqliteHelper = new SqliteHelper(this);
        getStudentsList();
    }

    private void initialiseViews() {
        rvMultipleChecks = findViewById(R.id.recTags);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMultipleChecks.setLayoutManager(layoutManager);
        materialSearchView = findViewById(R.id.materialSearchView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Select Multiple Students List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button btnCheckIn = findViewById(R.id.btnCheckin);
        Button btnCheckOut = findViewById(R.id.btnCheckout);
        btnCheckIn.setOnClickListener(this);
        btnCheckOut.setOnClickListener(this);

        Button btnSelectAll = findViewById(R.id.btnSelectAll);
        btnSelectAll.setOnClickListener(this);
    }

    private void getStudentsList() {
        mProgressDialog.show();
        sqliteHelper.getStudentsList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<String> idsList) {
                        mProgressDialog.dismiss();
                        if (idsList.size() > 0) {
                            for (String obj : idsList) {
                                studentsIdsList.add(new TagsStatePojo(obj, false));
                            }
                            getClassesList();
                            multiTagsSelectAdapter = new MultiTagsSelectAdapter(studentsIdsList, MultiChecksActivity.this);
                            rvMultipleChecks.setAdapter(multiTagsSelectAdapter);
                        } else {
                            Toast.makeText(MultiChecksActivity.this, "No Student's Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MultiChecksActivity.this, R.string.Error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_material_search, menu);
        MenuItem menuItem = menu.findItem(R.id.actionSearch);
        materialSearchView.setMenuItem(menuItem);
        materialSearchView.setOnQueryTextListener(this);
        materialSearchView.setHint("Search By Student Id");
        materialSearchView.setOnSearchViewListener(this);
        return true;
    }

    @Override
    public void onTagSelected(TagsStatePojo tagsStatePojo) {
        multiTagsList.add(tagsStatePojo.getStud_Id());
    }

    @Override
    public void onTagRemoved(TagsStatePojo tagsStatePojo) {
        multiTagsList.remove(tagsStatePojo.getStud_Id());

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        List<TagsStatePojo> filteredList = new ArrayList<>();
        for (TagsStatePojo obj : studentsIdsList) {
            if (obj.getStud_Id().trim().equalsIgnoreCase(query.toLowerCase())) {
                filteredList.add(obj);
            }
        }
        multiTagsSelectAdapter.refreshList(filteredList);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {
        multiTagsSelectAdapter.refreshList(studentsIdsList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCheckin:
                if (studentsIdsList.size() > 0)
                    checkIn();
                break;
            case R.id.btnCheckout:
                if (studentsIdsList.size() > 0)
                    checkOut();
                break;

            case R.id.btnSelectAll:
                selectAll();
                break;
        }
    }


    private void selectAll() {
        mProgressDialog.show();
        for (TagsStatePojo obj : studentsIdsList) {
            obj.setSelected(true);
            multiTagsList.add(obj.getStud_Id());
        }
        multiTagsSelectAdapter.refreshList(studentsIdsList);

        mProgressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkIn() {
        mProgressDialog.show();
        long currentTime = System.currentTimeMillis();
        List<Class> studentAttendanceList = new ArrayList<>();
        for (String obj : multiTagsList) {
            Class classPojo = new Class();
            classPojo.setStudId(obj);
            classPojo.setCheckInTime(getTime(currentTime));
            classPojo.setCheckInDate(getDate(currentTime));
            classPojo.setCheckInDateMilli(currentTime);
            studentAttendanceList.add(classPojo);
        }
        sqliteHelper.setMultipleStudentsCheckInRx(classList, studentAttendanceList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MultiChecksActivity.this, "Student's have been checkedIn", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MultiChecksActivity.this, R.string.Error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkOut() {
        mProgressDialog.show();
        long currentTime = System.currentTimeMillis();
        List<Class> studentAttendanceList = new ArrayList<>();
        for (String obj : multiTagsList) {
            Class classPojo = new Class();
            classPojo.setStudId(obj);
            classPojo.setCheckOutTime(getTime(currentTime));
            classPojo.setCheckOutDate(getDate(currentTime));
            studentAttendanceList.add(classPojo);
        }
        sqliteHelper.setMultipleStudentsCheckedOutRx(classList, studentAttendanceList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MultiChecksActivity.this, "Student's have been checkedOut", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MultiChecksActivity.this, R.string.Error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm", cal).toString();
        return date;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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
                                Toast.makeText(MultiChecksActivity.this, "Please upload data from settings", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(MultiChecksActivity.this, R.string.Error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
