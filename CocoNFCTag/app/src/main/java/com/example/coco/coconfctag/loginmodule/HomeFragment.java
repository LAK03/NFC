package com.example.coco.coconfctag.loginmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mLLayout1.setOnClickListener(this);
        mLLayout2.setOnClickListener(this);
        mLLayout3.setOnClickListener(this);
    }

    public void setListener(LockNavigationListener lis) {
        mLockNavLis = lis;
    }

    private void init(View v) {
        mLLayout1 = (LinearLayout) v.findViewById(R.id.llay1);
        mLLayout2 = (LinearLayout) v.findViewById(R.id.llay2);
        mLLayout3 = (LinearLayout) v.findViewById(R.id.llay3);
        mDB = new DatabaseHandler(getContext());
        mDB.addProduct(new ProductItem("501", "Dove Soap", 20, 1));
        mDB.addProduct(new ProductItem("502", "Dove Shampoo", 30, 1));
        mDB.addProduct(new ProductItem("503", "Fair & Lovely", 25, 1));
        mDB.addProduct(new ProductItem("504", "Fog Perfume", 50, 1));
        mDB.addProduct(new ProductItem("505", "Hair Oil", 40, 1));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llay1:
                openFrag(0);
                break;
            case R.id.llay2:
                openFrag(1);
                break;
            case R.id.llay3:
                openFrag(2);
                break;
        }
    }

    private void openFrag(int i) {
        Fragment firstFragment = null;
        switch (i) {
            case 0:
                firstFragment = new NFCRead1Fragment();
                break;
            case 1:
                firstFragment = new ScannedListFragment();
                ((ScannedListFragment) firstFragment).setListener(this, this);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productarray", mProductArray);
                firstFragment.setArguments(bundle);
                mLockNavLis.onFragmentOpen();
                break;
            case 2:
                firstFragment = new ScannedListFragment();
                ((ScannedListFragment) firstFragment).setListener(this, this);
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
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).getProductId().equals(productid)) {
                int count = mProductArray.get(i).getCount();
                mProductArray.get(i).setCount(count + quantity);

            }
        }

    }


    @Override
    public void onScanResult(JSONObject obj) {
        String id = obj.optString("id");
        ProductItem dbItem = mDB.getProductItem(id);
        if (dbItem != null) {
            if (mProductArray.size() > 0) {
                ProductItem item = null;
                for (int i = 0; i < mProductArray.size(); i++) {
                    if (mProductArray.get(i).getProductId().equals(id)) {
                        item = mProductArray.get(i);
                        int count = item.getCount();
                        item.setCount(count + 1);
                    }
                }
                if (item == null)
                    mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductPrice(), 1));
            } else {
                mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductPrice(), 1));
            }

        } else {
            Toast.makeText(getContext(), "Item not found on Database", Toast.LENGTH_SHORT).show();
        }

    }
}
