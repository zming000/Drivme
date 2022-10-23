package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class TouristNavActivity extends AppCompatActivity {
    //declare variables
    BottomNavigationView mbtmTNav;
    TabLayout mtabTouristActivity;
    ViewPager2 mvpActivity;
    AdapterTouristViewPager tvpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_activity);

        //assign variable
        mbtmTNav = findViewById(R.id.btmTNav);
        mtabTouristActivity = findViewById(R.id.tabTouristActivity);
        mvpActivity = findViewById(R.id.vpActivity);
        tvpAdapter = new AdapterTouristViewPager(this);
        mvpActivity.setAdapter(tvpAdapter);

        mtabTouristActivity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mvpActivity.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //nothing
            }
        });

        mvpActivity.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(mtabTouristActivity.getTabAt(position)).select();
            }
        });

        navSelection();
    }

    private void navSelection() {
        //set homepage selected
        mbtmTNav.setSelectedItemId(R.id.activity);

        //perform listener
        mbtmTNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
                    return true;
                case R.id.cars:
                    startActivity(new Intent(getApplicationContext(), TouristNavCars.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), TouristNavHomepage.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), TouristNavSettings.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
            }
            return false;
        });
    }

    //quit application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Drivme?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finishAffinity();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}