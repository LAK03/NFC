package com.example.coco.coconfctag.readermodule;

import android.content.Intent;
import android.graphics.Point;
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
import com.example.coco.coconfctag.barcode.BarcodeCaptureActivity;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

public class BarReadFragment extends Fragment implements View.OnClickListener {
    private TextView mReadBarTxt;
    private static final String LOG_TAG = BarReadFragment.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private TextView mId, mName, mPrice;
    private CardView mCardView;
    private DatabaseHandler mDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barread, container, false);

        init(view);
        setListeners();
        return view;
    }

    private void setListeners() {
        mReadBarTxt.setOnClickListener(this);
    }

    private void init(View view) {
        mReadBarTxt = (TextView) view.findViewById(R.id.read_bar_txt);

        mId = (TextView) view.findViewById(R.id.id_txt);
        mName = (TextView) view.findViewById(R.id.name_txt);
        mPrice = (TextView) view.findViewById(R.id.price_txt);
        mCardView = (CardView) view.findViewById(R.id.card_view);
        mDB = new DatabaseHandler(getContext());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_bar_txt:
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                break;
            case R.id.read_qr_txt:
                Intent intents = new Intent(getContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intents, BARCODE_READER_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("LAG", "dddddddddddd");
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(barcode.displayValue);
                        Log.e(LOG_TAG, "d");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "d");
                        e.printStackTrace();
                    }
                    doJSONParsing(obj);

                } else
                    Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format), CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doJSONParsing(JSONObject obj) {
        try {
            Log.e(LOG_TAG, "d");
            String id = obj.getString("id");

            ProductItem dbItem = mDB.getProductItem(id);
            if (dbItem != null) {
                mCardView.setVisibility(View.VISIBLE);
                mName.setText(dbItem.getProductName());
                mPrice.setText("$ " + dbItem.getProductPrice());
                mId.setText("" + dbItem.getProductId());
            } else {
                Toast.makeText(getContext(), "Item not found on database", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
