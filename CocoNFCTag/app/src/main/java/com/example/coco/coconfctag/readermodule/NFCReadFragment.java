package com.example.coco.coconfctag.readermodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;

import org.json.JSONException;
import org.json.JSONObject;

public class NFCReadFragment extends Fragment {

    private DatabaseHandler mDB;

    public static NFCReadFragment newInstance() {

        return new NFCReadFragment();
    }

    private TextView mId, mName, mPrice;
    private CardView mCardView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nfcread, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        mId = (TextView) view.findViewById(R.id.id_txt);
        mName = (TextView) view.findViewById(R.id.name_txt);
        mPrice = (TextView) view.findViewById(R.id.price_txt);
        mCardView = (CardView) view.findViewById(R.id.card_view);
        mDB = new DatabaseHandler(getContext());
    }


    public void setNFCText(String result) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            Log.e("ji", "ll");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (obj != null)
            addToCart(obj);
        Log.e("ji", "ll");
    }

    private void addToCart(JSONObject obj) {

        String id = obj.optString("id");
        ProductItem dbItem = mDB.getProductItem(id);
        if (dbItem != null) {
            mCardView.setVisibility(View.VISIBLE);
            mName.setText(dbItem.getProductName());
            mPrice.setText("$ " + dbItem.getProductPrice());
            mId.setText("" + dbItem.getProductId());

        }
        else
        {
            Toast.makeText(getContext(),"Item not found on database",Toast.LENGTH_SHORT).show();
        }
    }
}