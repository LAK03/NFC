package com.example.coco.coconfctag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TabLayout                       csSetingsTabLay     = null;
    HorizontalScrollView            csScrollView        = null;
    ViewPager                       viewPager           = null;
    PagerAdapter                    csPagerAdapter      = null;
    LinearLayout                    csTabLay            = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getActivityResources();
        createTab();


    }


    private void getActivityResources()
    {
        csTabLay          = (LinearLayout)findViewById(R.id.contentMain_tabMainLay);
        csSetingsTabLay   = (TabLayout)findViewById(R.id.contentMain_tab_layout);
        csScrollView      = (HorizontalScrollView)findViewById(R.id.contentMain_scrollView);
    }

    private void createTab()
    {
        ArrayList<String> tabs = new ArrayList<>();

        tabs.add("IOT NFC Tags                 ");

        tabs.add("Action Settings");
        //tabs.add("Tuesday");
        /*tabs.add("Wednesday");
        tabs.add("Thursday");
        tabs.add("Friday");
        tabs.add("Saturday");*/


        final TabLayout tabLayout = (TabLayout)findViewById(R.id.contentMain_tab_layout);

        for(String tbString : tabs)
        {
            tabLayout.addTab(tabLayout.newTab().setText(tbString));

        }

        viewPager = (ViewPager)findViewById(R.id.contentMain_tab_pager);

        csPagerAdapter = new PagerAdapter(getSupportFragmentManager(),MainActivity.this,tabs);

        viewPager.setAdapter(csPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //if you want to focus particular tab on open yhen add below line
        //viewPager.setCurrentItem(1);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //code to auto scroll tablayout
                if(tabLayout.getSelectedTabPosition()>2) {;
                    csScrollView.fullScroll(View.FOCUS_RIGHT);
                }
                else if(tabLayout.getSelectedTabPosition()<4) {
                    csScrollView.fullScroll(View.FOCUS_LEFT);
                }
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }

        });


    }



}
