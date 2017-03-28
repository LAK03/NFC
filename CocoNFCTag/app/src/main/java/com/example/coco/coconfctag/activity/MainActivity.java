package com.example.coco.coconfctag.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.coco.coconfctag.PagerAdapter;
import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.Tab1;
import com.example.coco.coconfctag.Tab2;
import com.example.coco.coconfctag.common.ViewPagerAdapter;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.readermodule.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DatabaseHandler mDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mDB = new DatabaseHandler(this);
        mDB.addProduct(new ProductItem("501", "Dove Soap", 20, 1));
        mDB.addProduct(new ProductItem("502", "Dove Shampoo", 30, 1));
        mDB.addProduct(new ProductItem("503", "Fair & Lovely", 25, 1));
        mDB.addProduct(new ProductItem("504", "Fog Perfume", 50, 1));
        mDB.addProduct(new ProductItem("505", "Hair Oil", 40, 1));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        Tab1 afrag = new Tab1();
        Tab2 safrag = new Tab2();
        adapter.addFragment(afrag, "IOT NFC Tags");
        adapter.addFragment(safrag, "Action Settings");
        viewPager.setAdapter(adapter);
    }

}
