package com.example.coco.coconfctag.loginmodule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;

import static android.content.Context.MODE_PRIVATE;

public class IndividualItemFragment extends Fragment implements View.OnClickListener {

    private ProductItem item;
    private TextView mProductName, mProductPrice;
    private ImageView mProductImg;
    private TextView mTitleTxtView;
    private Button mAddWishlistBtn;
    private DatabaseHandler mDb;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            item = bundle.getParcelable("item");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_individual_item, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mAddWishlistBtn.setOnClickListener(this);
    }

    private void init(View v) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mProductName = (TextView) v.findViewById(R.id.productname_txt);
        prefs = getContext().getSharedPreferences("cocosoft", MODE_PRIVATE);
        mProductPrice = (TextView) v.findViewById(R.id.productprice_txt);
        mProductImg = (ImageView) v.findViewById(R.id.product_img);
        mAddWishlistBtn = (Button) v.findViewById(R.id.add_wish_btn);
        mProductName.setText(item.getProductName());
        mProductPrice.setText("$ " + item.getProductPrice());
        if (item.getProductId().equals("501"))
            mProductImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_dovesoap));
        else if (item.getProductId().equals("502"))
            mProductImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_doveshampoo));
        else if (item.getProductId().equals("503"))
            mProductImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_fairnlovely));
        else if (item.getProductId().equals("504"))
            mProductImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_fog));
        else if (item.getProductId().equals("505"))
            mProductImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_hairoil));
        mTitleTxtView.setText(item.getProductName());
        mDb = new DatabaseHandler(getContext());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_wish_btn:
                boolean isloggedin = prefs.getBoolean("isloggedin", false);
                String username = prefs.getString("username", "");
                if (isloggedin) {
                    if (!(mDb.wishlistAlreadyAdded(username, item.getProductId()))) {
                        mDb.addToWishlist(item.getProductId(), username);
                        Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
