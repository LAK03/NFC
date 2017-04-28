package com.example.coco.coconfctag.loginmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;

import org.json.JSONException;
import org.json.JSONObject;

public class NFCRead1Fragment extends Fragment {

    private DatabaseHandler mDB;

    public static NFCRead1Fragment newInstance() {

        return new NFCRead1Fragment();
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nfcread1, container, false);
        init(view);
        return view;
    }

    private void init(View view) {


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


        }
        else
        {
            Toast.makeText(getContext(),"Item not found on database",Toast.LENGTH_SHORT).show();
        }
    }
}