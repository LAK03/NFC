package com.example.coco.coconfctag.wishlistmodule;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.cartmodule.CartItem;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.CheckboxListener;
import com.example.coco.coconfctag.listeners.WishlistListener;
import com.example.coco.coconfctag.loginmodule.LoginFragment;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WishListFragment extends Fragment implements View.OnClickListener, WishlistListener, CheckboxListener {

    private TextView mAddCartTxt;
    private LinearLayoutManager mLManager;
    private RecyclerView mProductRView;
    private WishlistAdapter mWishlistAdapter;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private DatabaseHandler mDb;
    private ArrayList<WishlistItem> mWishlistArray = new ArrayList<>();
    private Gson gson;
    private ArrayList<CartItem> mCartArray=new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        gson=new Gson();
        String tempdata = prefs.getString("tempcartlist", null);
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        ArrayList<CartItem> arr=gson.fromJson(tempdata, type);
        if(arr!=null) {
            mCartArray = gson.fromJson(tempdata, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("WishList");
    }

    private void setListeners() {
        mAddCartTxt.setOnClickListener(this);

    }


    private void init(View view) {
        mAddCartTxt = (TextView) view.findViewById(R.id.add_cart_txt);
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) view.findViewById(R.id.rview);
        mProductRView.setLayoutManager(mLManager);
        mDb = new DatabaseHandler(getContext());
        String username = prefs.getString("username", null);
        ArrayList<String> productids = mDb.getAllWishList(username);
        if (productids != null) {
            for (int i = 0; i < productids.size(); i++) {
                mProductArray.add(mDb.getProductItem(productids.get(i)));
            }
        }
        mWishlistAdapter = new WishlistAdapter(getContext(), mProductArray, this, this);
        mProductRView.setAdapter(mWishlistAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);
        mCountTxtView.setVisibility(View.GONE);
        mCartImg.setVisibility(View.GONE);
        for (int j = 0; j < mProductArray.size(); j++) {
            mWishlistArray.add(j, new WishlistItem(mProductArray.get(j).getProductId(), false));
        }
    }


    @Override
    public void onClick(View v) {
        boolean isloggedin = prefs.getBoolean("isloggedin",false);
        String username = prefs.getString("username", "");
        switch (v.getId()) {
            case R.id.add_cart_txt:
                Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                addToCart();
                break;
        }
    }

    private void addToCart() {

        for(int i=0;i<mWishlistArray.size();i++)
        {
            if(mWishlistArray.get(i).isChecked()) {
                CartItem cartItem = null;
                for (int j = 0; j < mCartArray.size(); j++) {
                    if (mWishlistArray.get(i).getProductid().equals(mCartArray.get(j).getProductId())) {
                        cartItem = mCartArray.get(j);
                    }
                }
                if (cartItem != null) {
                    int count = cartItem.getCount();
                    cartItem.setCount(count + 1);
                } else {
                    ProductItem it=mDb.getProductItem(mWishlistArray.get(i).getProductid());
                    mCartArray.add(new CartItem(it.getProductId(),it.getProductName(),it.getProductPrice(),1,it.getScantype(),it.isChecked()));
                }
            }
        }

        String json = gson.toJson(mCartArray);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();
    }


    private void openFrag(int i) {
        Fragment firstFragment = null;
        switch (i) {
            case 1:
                firstFragment = new LoginFragment();
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
        mDb.removeWishlist(productid, username);
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).getProductId().equals(productid))
                mProductArray.remove(i);
        }
        mWishlistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChecked(String productid, boolean isChecked) {
        for (int i = 0; i < mWishlistArray.size(); i++) {
            if (mWishlistArray.get(i).getProductid().equals(productid)) {
                mWishlistArray.get(i).setChecked(isChecked);
            }
        }
    }

    public class WishlistItem {

        private String productid = "";
        private boolean isChecked = false;

        public WishlistItem(String productid, boolean isChecked) {
            this.productid = productid;
            this.isChecked = isChecked;
        }

        public WishlistItem() {
        }

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
