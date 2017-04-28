package com.example.coco.coconfctag.cartmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.CheckboxListener;
import com.example.coco.coconfctag.listeners.IndividualItemListener;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.listeners.WishlistListener;
import com.example.coco.coconfctag.loginmodule.IndividualItemFragment;
import com.example.coco.coconfctag.loginmodule.LoginFragment;
import com.example.coco.coconfctag.scanlistmodule.ScanListAdapter;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CartFragment extends Fragment implements View.OnClickListener, QuantityListener, WishlistListener, IndividualItemListener, CheckboxListener {

    private TextView mAddCartTxt, mGrandTotalTxt;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private CartAdapter mCartAdapter;
    private QuantityListener mQtyListener;
    private ArrayList<CartItem> mCartArray;

    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private SharedPreferences prefs;
    private TextView mAddWishTxt;
    private Gson gson;
    private SharedPreferences.Editor prefsEditor;
    private DatabaseHandler mDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCartArray = getArguments().getParcelableArrayList("cartarray");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("Cart");
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);
        mAddWishTxt.setOnClickListener(this);
    }

    public void setListener(QuantityListener lis) {
        mQtyListener = lis;
    }

    private void init(View view) {
        gson = new Gson();
        mAddCartTxt = (TextView) view.findViewById(R.id.add_cart_txt);
        mAddWishTxt = (TextView) view.findViewById(R.id.add_wish_txt);
        mGrandTotalTxt = (TextView) view.findViewById(R.id.grandtotal_txt);
        mDB=new DatabaseHandler(getContext());
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mCartAdapter = new CartAdapter(getContext(), mCartArray, this, this, this,this);
        mProductRView.setAdapter(mCartAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        calculateTotal();
    }


    @Override
    public void onClick(View v) {
        boolean isloggedin = prefs.getBoolean("isloggedin", false);
        String username = prefs.getString("username", "");
        switch (v.getId()) {
            case R.id.add_cart_txt:

                if (isloggedin) {
                    Toast.makeText(getContext(), "Processing Payment", Toast.LENGTH_SHORT).show();
                } else {
                    openFrag(1, "");
                }
                break;

        }
    }



    @Override
    public void onQuantityChange(String productid, int quantity) {
        if (quantity == 0) {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(productid)) {
                    mCartArray.get(i).setCount(0);
                    mCartArray.remove(i);
                }
            }
        } else {
            for (int i = 0; i < mCartArray.size(); i++) {
                if (mCartArray.get(i).getProductId().equals(productid)) {
                    int count = mCartArray.get(i).getCount();
                    mCartArray.get(i).setCount(count + quantity);
                }
            }
        }
        mCartAdapter.notifyDataSetChanged();
        calculateTotal();
    }

    private void calculateTotal() {
        int total = 0;
        for (int i = 0; i < mCartArray.size(); i++) {
            total = total + (mCartArray.get(i).getCount() * mCartArray.get(i).getProductPrice());
        }
        mGrandTotalTxt.setText("Grand Total = $ " + total);
    }

    private void openFrag(int i, String productid) {
        Fragment firstFragment = null;
        switch (i) {
            case 1:
                firstFragment = new LoginFragment();
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

    }
}
