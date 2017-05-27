package com.example.coco.coconfctag.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.cartmodule.CartFragment;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.listeners.ScanResultListener;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.example.coco.coconfctag.scanlistmodule.ScannedListFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cocoadmin on 3/16/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, QuantityListener, ScanResultListener {

    private LinearLayout mLLayout1, mLLayout2, mLLayout3;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private TextView mTitleTxtView;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private TextView mCountTxtView;
    private ImageView mCartImg;
    Fragment firstFragment = null;
    private int scanTypeFlag = 0;
    private DatabaseHandler mDB;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        gson = new Gson();
        String tempdata = prefs.getString("tempscanlist", null);
        Type type = new TypeToken<List<ProductItem>>() {
        }.getType();
        ArrayList<ProductItem> arr = gson.fromJson(tempdata, type);
        if (arr != null) {
            mProductArray = gson.fromJson(tempdata, type);
        }
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
        mDB = new DatabaseHandler(getContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llay1:
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "NFC Enabled", Snackbar.LENGTH_SHORT);
                snackbar.show();
                scanTypeFlag = 1;
                break;
            case R.id.llay2:
                scanTypeFlag = 2;
                openFrag(1, true);
                break;
            case R.id.llay3:
                scanTypeFlag = 3;
                openFrag(1, true);
                break;
            case R.id.cart_img:
                openFrag(2, false);
                break;
        }
    }

    private void openFrag(int i, boolean cameraflag) {
        switch (i) {
            case 1:
                firstFragment = new ScannedListFragment();
                ((ScannedListFragment) firstFragment).setInterface(this, this);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productarray", mProductArray);
                bundle.putBoolean("isscan", cameraflag);
                bundle.putInt("scantype", scanTypeFlag);
                firstFragment.setArguments(bundle);
                break;
            case 2:
                firstFragment = new CartFragment();
                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
    }


    public void openScanListFrag(JSONObject jsonObject, int flag) {
        onScanResult(jsonObject, flag);
        openFrag(1, false);
    }


    @Override
    public void onQuantityChange(String productid, int quantity) {
        if (quantity == 0) {
            for (int i = 0; i < mProductArray.size(); i++) {
                if (mProductArray.get(i).getProductId().equals(productid)) {
                    mProductArray.remove(i);
                }
            }
        } else {
            for (int i = 0; i < mProductArray.size(); i++) {
                if (mProductArray.get(i).getProductId().equals(productid)) {
                    int count = mProductArray.get(i).getCount();
                    mProductArray.get(i).setCount(count + quantity);
                }
            }
        }
        String json = gson.toJson(mProductArray);
        prefsEditor.putString("tempscanlist", json);
        prefsEditor.commit();
    }

    @Override
    public void onScanResult(JSONObject obj, int scantype) {
        String id = obj.optString("id");
        ProductItem dbItem = mDB.getProductItem(id);
        if (dbItem != null) {
            if (mProductArray.size() > 0) {
                ProductItem item = null;
                for (int i = 0; i < mProductArray.size(); i++) {
                    if (mProductArray.get(i).getProductId().equals(id)) {
                        item = mProductArray.get(i);
                        Toast.makeText(getContext(), item.getProductName() + " added", Toast.LENGTH_SHORT).show();
                        int count = item.getCount();
                        item.setCount(count + 1);
                        item.setScantype(scantype);
                    }
                }
                if (item == null) {
                    mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductPrice(), 1, scantype, false));
                }
            } else {
                mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductPrice(), 1, scantype, false));
            }
        } else {
            Toast.makeText(getContext(), "Item not found on Database", Toast.LENGTH_SHORT).show();
        }
        String json = gson.toJson(mProductArray);
        prefsEditor.putString("tempscanlist", json);
        prefsEditor.commit();
    }
}
