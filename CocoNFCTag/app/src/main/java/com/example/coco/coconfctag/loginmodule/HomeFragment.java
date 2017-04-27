package com.example.coco.coconfctag.loginmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.LockNavigationListener;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.listeners.ScanResultListener;
import com.example.coco.coconfctag.readermodule.ProductItem;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cocoadmin on 3/16/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, QuantityListener, ScanResultListener {


    private LinearLayout mLLayout1, mLLayout2, mLLayout3;
    private DatabaseHandler mDB;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private LockNavigationListener mLockNavLis;
    private TextView mTitleTxtView;
    private CoordinatorLayout coordinatorLayout;
    private ScanResultListener mScanListener;
    private QuantityListener mQuantityListener;
    private TextView mCountTxtView;
    private ImageView mCartImg;
    Fragment firstFragment = null;
    private int scanTypeFlag=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductArray = getArguments().getParcelableArrayList("productarray");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        setListeners();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Easy Shopping Cart");
    }

    private void setListeners() {
        mLLayout1.setOnClickListener(this);
        mLLayout2.setOnClickListener(this);
        mLLayout3.setOnClickListener(this);
    }

    public void setListener(LockNavigationListener lis, QuantityListener qlis, ScanResultListener scanlis) {
        mLockNavLis = lis;
        mQuantityListener = qlis;
        mScanListener = scanlis;
    }

    private void init(View v) {
        mLLayout1 = (LinearLayout) v.findViewById(R.id.llay1);
        mLLayout2 = (LinearLayout) v.findViewById(R.id.llay2);
        mLLayout3 = (LinearLayout) v.findViewById(R.id.llay3);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView.setText("Easy Shopping Cart");
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCartImg.setOnClickListener(this);
        mCountTxtView.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llay1:
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "NFC Enabled", Snackbar.LENGTH_SHORT);
                snackbar.show();
                break;
            case R.id.llay2:
                openFrag(1,true);
                scanTypeFlag=2;
                break;
            case R.id.llay3:
                openFrag(1,true);
                scanTypeFlag=3;
                break;
            case R.id.cart_img:

                openFrag(2,false);

                break;
        }
    }

    private void openFrag(int i,boolean cameraflag) {

        switch (i) {
            case 0:
                firstFragment = new NFCRead1Fragment();
                break;
            case 1:
                firstFragment = new ScannedListFragment();
                ((ScannedListFragment) firstFragment).setListener(this, this);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productarray", mProductArray);
                bundle.putBoolean("isscan", cameraflag);
                firstFragment.setArguments(bundle);
                mLockNavLis.onFragmentOpen();
                break;
            case 2:
                firstFragment = new CartFragment();
                ((CartFragment) firstFragment).setListener(this);
                Bundle bundles = new Bundle();
                bundles.putParcelableArrayList("productarray", mProductArray);
                firstFragment.setArguments(bundles);
                mLockNavLis.onFragmentOpen();
                break;


        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }

    @Override
    public void onQuantityChange(String productid, int quantity) {
        mQuantityListener.onQuantityChange(productid, quantity);
    }

    @Override
    public void onScanResult(JSONObject obj,int scantype) {
        mScanListener.onScanResult(obj,scanTypeFlag);
    }

    public void openScanListFrag(JSONObject jsonObject) {
        openFrag(1,false);
    }


}
