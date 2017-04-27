package com.example.coco.coconfctag.loginmodule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.barcode.BarcodeCaptureActivity;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.IndividualItemListener;
import com.example.coco.coconfctag.listeners.LockNavigationListener;
import com.example.coco.coconfctag.listeners.ScanResultListener;
import com.example.coco.coconfctag.listeners.WishlistListener;
import com.example.coco.coconfctag.multireadmodule.CartProductAdapter;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.readermodule.ProductItem;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ScannedListFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener {

    private Button mAddCartTxt;
    private static final String LOG_TAG = ScannedListFragment.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private DatabaseHandler mDB;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private CartProductAdapter mProductAdapter;
    private SharedPreferences prefs;
    private QuantityListener mQtyListener;
    private ScanResultListener mScanResultLis;
    private ArrayList<ProductItem> mProductArray;
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private boolean isScan = false;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductArray = getArguments().getParcelableArrayList("productarray");
        isScan = getArguments().getBoolean("isscan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scannedlist, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Product List");
        mCountTxtView.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.VISIBLE);
        changeCount();
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);

    }

    public void setListener(QuantityListener lis, ScanResultListener scanlis) {
        mQtyListener = lis;
        mScanResultLis = scanlis;

    }

    private void init(View view) {
        mAddCartTxt = (Button) view.findViewById(R.id.add_cart_txt);

        mDB = new DatabaseHandler(getContext());
        mLManager = new LinearLayoutManager(getContext());

        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mProductAdapter = new CartProductAdapter(getContext(), mProductArray, this, this,this);
        mProductRView.setAdapter(mProductAdapter);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCartImg.setOnClickListener(this);
        mCountTxtView.setVisibility(View.VISIBLE);
        mCartImg.setVisibility(View.VISIBLE);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mSearchLayout = (RelativeLayout) getActivity().findViewById(R.id.search_layout);
        mSearchLayout.setVisibility(View.GONE);
        if (isScan) {

            Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }


    }


    @Override
    public void onClick(View v) {
        boolean isloggedin = prefs.getBoolean("isloggedin", false);
        switch (v.getId()) {
            case R.id.add_cart_txt:
              /*  boolean isloggedin = prefs.getBoolean("isloggedin", false);
                if (isloggedin) {
                    changeCount();
                } else {
                    openFrag(1);
                }*/
                changeCount();
                break;
            case R.id.cart_img:
                openFrag(0,"");
                break;
        }
    }


    private void changeCount() {
        int mCount = 0;
        int Total = 0;
        for (int i = 0; i < mProductArray.size(); i++) {
            mCount = mCount + mProductArray.get(i).getCount();
            Total = Total + (mProductArray.get(i).getCount() * mProductArray.get(i).getProductPrice());
        }
        mCountTxtView.setText("" + mCount);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            Log.e(LOG_TAG, "dddddddddd");
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(barcode.displayValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (obj != null) {
                        mScanResultLis.onScanResult(obj, 0);
                        mProductAdapter.notifyDataSetChanged();
                        String id = obj.optString("id");
                        ProductItem dbItem = mDB.getProductItem(id);
                        if (dbItem != null) {
                            if (mProductArray.size() > 0) {
                                for (int i = 0; i < mProductArray.size(); i++) {
                                    if (mProductArray.get(i).getProductId().equals(id)) {
                                      /*  Snackbar snackbar = Snackbar.make(coordinatorLayout, mProductArray.get(i).getProductName()+" added", Snackbar.LENGTH_LONG);
                                        snackbar.show();*/
                                    }
                                }


                            }

                        }
                    }
                } else
                    Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format), CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onQuantityChange(String id, int quantity) {

        mQtyListener.onQuantityChange(id, quantity);
        mProductAdapter.notifyDataSetChanged();
        changeCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        isScan=false;
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 0:
                firstFragment = new CartFragment();
                ((CartFragment) firstFragment).setListener(this);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("productarray", mProductArray);
                firstFragment.setArguments(bundle);
                break;

            case 1:
                firstFragment = new LoginFragment();
                break;
            case 3:
                firstFragment = new WishListFragment();
                break;
            case 2:
                firstFragment=new IndividualItemFragment();
                ProductItem item=mDB.getProductItem(productid);
                Bundle bundles = new Bundle();
                bundles.putParcelable("item", item);
                firstFragment.setArguments(bundles);
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
    public void onFavouriteClicked(String productid, boolean isChecked) {

        String username = prefs.getString("username", "");
        if (isChecked) {

                Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
                mDB.addToWishlist(productid, username);

        }
        else
        {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
                mDB.removeWishlist(productid, username);

        }


    }

    @Override
    public void OnCardClick(String productid) {
        openFrag(2,productid);
    }
}
