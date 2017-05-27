package com.example.coco.coconfctag.scanlistmodule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.barcode.BarcodeCaptureActivity;
import com.example.coco.coconfctag.cartmodule.CartItem;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.CheckboxListener;
import com.example.coco.coconfctag.listeners.IndividualItemListener;
import com.example.coco.coconfctag.listeners.ScanResultListener;
import com.example.coco.coconfctag.listeners.WishlistListener;
import com.example.coco.coconfctag.cartmodule.CartFragment;
import com.example.coco.coconfctag.loginmodule.IndividualItemFragment;
import com.example.coco.coconfctag.loginmodule.LoginFragment;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.wishlistmodule.WishListFragment;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ScannedListFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener, CheckboxListener, ScanResultListener {

    private Button mAddCartTxt;
    private static final String LOG_TAG = ScannedListFragment.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private DatabaseHandler mDB;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private ScanListAdapter mScanListAdapter;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private ArrayList<CartItem> mCartArray= new ArrayList<>() ;
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private boolean isScan = false;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;
    private int scantype;
    private QuantityListener mQuantityLis;
    private ScanResultListener mScanResultLis;

    private Gson gson;
    private CheckBox mSelectAllChkBox;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductArray = getArguments().getParcelableArrayList("productarray");
        isScan = getArguments().getBoolean("isscan");
        scantype = getArguments().getInt("scantype");
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();

    }

    public void setInterface(QuantityListener qlis, ScanResultListener scanlis) {
        this.mQuantityLis = qlis;
        this.mScanResultLis = scanlis;
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

    private void changeCount() {
        int mCount=0;
        mCartArray=new ArrayList<>();
        String tempdata = prefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        ArrayList<CartItem> arr=gson.fromJson(tempdata, type);
        if(arr!=null) {
            mCartArray = gson.fromJson(tempdata, type);
        }
        for (int i = 0; i < mCartArray.size(); i++) {
            if (mCartArray.get(i).getCount()>0) {
                {
                    mCount = mCount + mCartArray.get(i).getCount();
                }

            }
        }
        mCountTxtView.setText("" + mCount);
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);
        mSelectAllChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    for (int i = 0; i < mProductArray.size(); i++) {
                        {
                            mProductArray.get(i).setChecked(isChecked);
                            mScanListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < mProductArray.size(); i++) {
                        {
                            mProductArray.get(i).setChecked(false);
                            mScanListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }


    private void init(View view) {
        gson = new Gson();
        mAddCartTxt = (Button) view.findViewById(R.id.add_cart_txt);
        mSelectAllChkBox = (CheckBox) view.findViewById(R.id.selectall);
        mDB = new DatabaseHandler(getContext());
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mScanListAdapter = new ScanListAdapter(getContext(), mProductArray, this, this, this, this);
        mProductRView.setAdapter(mScanListAdapter);
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
                addToCart();
                changeCount();
                openFrag(0, "");
                break;
            case R.id.cart_img:
                openFrag(0, "");
                break;
        }
    }


    private void addToCart() {
        mCartArray = new ArrayList<>();
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).isChecked()) {
                {
                    mCartArray.add(new CartItem(mProductArray.get(i).getProductId(),mProductArray.get(i).getProductName(),mProductArray.get(i).getProductPrice(),mProductArray.get(i).getCount(),mProductArray.get(i).getScantype(),mProductArray.get(i).isChecked()));
                }
            }
        }
        String json = gson.toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
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
                        onScanResult(obj, scantype);
                        mScanListAdapter.notifyDataSetChanged();


                    }
                } else
                    Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format), CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onQuantityChange(String productid, int quantity) {
        mQuantityLis.onQuantityChange(productid, quantity);
        mScanListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        isScan = false;
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 0:
                firstFragment = new CartFragment();
                ((CartFragment) firstFragment).setListener(this);
                Bundle bundle = new Bundle();
                firstFragment.setArguments(bundle);
                break;

            case 1:
                firstFragment = new LoginFragment();
                break;

            case 3:
                firstFragment = new WishListFragment();
                break;

            case 2:
                firstFragment = new IndividualItemFragment();
                ProductItem item = mDB.getProductItem(productid);
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

        } else {
            Toast.makeText(getContext(), "Wishlist Removed", Toast.LENGTH_SHORT).show();
            mDB.removeWishlist(productid, username);

        }


    }

    @Override
    public void OnCardClick(String productid) {
        openFrag(2, productid);
    }

    @Override
    public void onChecked(String productid, boolean isChecked) {
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).getProductId().equals(productid)) {
                mProductArray.get(i).setChecked(isChecked);
            }
        }
    }

    @Override
    public void onScanResult(JSONObject obj, int scantype) {
        mScanResultLis.onScanResult(obj, scantype);
        mScanListAdapter.notifyDataSetChanged();
    }
}
