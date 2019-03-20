package com.projects.automatedattendancesystem;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projects.automatedattendancesystem.Pojo.NavigationMenuPojo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static FragmentManager manager;
    public static Fragment mFragment;
    private DrawerLayout drawyerLayout;
    private ActionBarDrawerToggle toggle;
    private ListView lvMenu;
    private TextView tvCollegename;

    public static void replaceFragment(Fragment fragment) {

        FragmentTransaction transaction = manager.beginTransaction();//create an instance of Fragment-transaction
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mFragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("address", "" + DebugDB.getAddressLog());
        initialiseViews();

        drawyerLayout = findViewById(R.id.drawyerLayout);
        tvCollegename = findViewById(R.id.tvCollegename);
        Preferences.loadPreferences(this);
        if (!TextUtils.isEmpty(Preferences.SCHOOL_NAME)) {
            tvCollegename.setText(Preferences.SCHOOL_NAME);
        }else {
            tvCollegename.setText("Automated Attendance System");
        }

        lvMenu = findViewById(R.id.lvMenu);
        toggle = new ActionBarDrawerToggle(this, drawyerLayout, toolbar, R.string.open, R.string.Close);
        drawyerLayout.addDrawerListener(toggle);
        toggle.syncState();

        final List<NavigationMenuPojo> menuList = new ArrayList<>();
        menuList.add(new NavigationMenuPojo("Attendance", 1));
        menuList.add(new NavigationMenuPojo("Report", 2));
        menuList.add(new NavigationMenuPojo("Settings", 3));

        NavigationMenuListAdapter navigationMenuListAdapter = new NavigationMenuListAdapter(this, menuList);
        lvMenu.setAdapter(navigationMenuListAdapter);

        lvMenu.setOnItemClickListener((adapterView, view, i, l) -> {
            int positionId = menuList.get(i).getPosition();
            drawyerLayout.closeDrawers();
            switch (positionId) {
                case 1:
                    replaceFragment(new UploadAttendanceFragment());
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    getSupportActionBar().setTitle("Upload Attendance");
                    break;
                case 2:
                    replaceFragment(new GenerateReportFragment());
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    getSupportActionBar().setTitle("Generate Report");
                    break;
                case 3:
                    replaceFragment(new SettingsFragment());
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    getSupportActionBar().setTitle("Upload Data");
                    break;
            }
        });
        replaceFragment(new UploadAttendanceFragment());
    }

    private void initialiseViews() {
        ImageView ivLogo = findViewById(R.id.ivLogo);
        Glide.with(this).asDrawable().load(R.drawable.logo).apply(RequestOptions.circleCropTransform()).into(ivLogo);
    }

    private class NavigationMenuListAdapter extends BaseAdapter {

        private List<NavigationMenuPojo> menuList;

        private LayoutInflater inflater;

        public NavigationMenuListAdapter(Context context, List<NavigationMenuPojo> menuList) {
            this.menuList = menuList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final NavigationMenuListAdapter.ViewHolder holder = new NavigationMenuListAdapter.ViewHolder();
            View rowView;
            rowView = inflater.inflate(R.layout.row_item_menu, null);
            holder.tvMenuName = rowView.findViewById(R.id.tvMenu);
            holder.tvMenuName.setText(menuList.get(i).getMenuName());
            return rowView;
        }

        class ViewHolder {
            TextView tvMenuName;
        }
    }


}
