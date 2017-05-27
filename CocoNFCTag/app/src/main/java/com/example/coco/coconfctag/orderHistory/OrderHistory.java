package com.example.coco.coconfctag.orderHistory;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

/**
 * Created by Guest1 on 5/23/2017.
 */

public class OrderHistory extends Fragment implements View.OnClickListener {

    private orderHistoryAdaptor mHistoryAdapter;
    private RecyclerView mProductRView;
    private LinearLayoutManager mLManager;

    private TextView mCountTxtView;
    private TextView mTitleTxtView;
    private ImageView mCartImg;
    private RelativeLayout mSearchLayout;

    private TextView _noOfOrders;
    private ListView _lView;


    DatabaseHandler mDb;
    int noOfOrders =0;
    ArrayList<orderHistoryItems> orderItems = new ArrayList<orderHistoryItems>();
    ArrayList<orderedProducts> productInfo = new ArrayList<orderedProducts>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        init(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleTxtView.setText("ORDER HISTORY");
    }
    private void setListeners()
    {

    }

   private void init (View v)
    {



        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mCountTxtView = (TextView) toolbar.findViewById(R.id.total_count);
        mTitleTxtView = (TextView) toolbar.findViewById(R.id.title_txt);
        mCartImg = (ImageView) toolbar.findViewById(R.id.cart_img);


        mDb = new DatabaseHandler(getContext());
        _noOfOrders = (TextView)v.findViewById(R.id.monOrderCnt);
        displayHistory();
        mLManager = new LinearLayoutManager(getContext());
        mProductRView = (RecyclerView) v.findViewById(R.id.rview);

        mProductRView.setLayoutManager(mLManager);
        mHistoryAdapter = new orderHistoryAdaptor(getContext(),orderItems );
        mProductRView.setAdapter(mHistoryAdapter);









    }
    private void displayHistory()
    {

        Cursor c1 =mDb.getOrderNumbers();

        int i=c1.getCount();
        Log.e("ORDER_REFNO count",String.valueOf(i));


            while (c1.moveToNext()) {


                Log.d("Order Number", c1.getString(c1.getColumnIndex(mDb.ORDER_REFNO)));
                Log.d("Order date", c1.getString(c1.getColumnIndex(mDb.ENTRYDATE)));
                Log.d("Order total"+ c1.getString(c1.getColumnIndex(mDb.ORDER_TOTAL_AMOUNT)),"");

                orderItems.add(new orderHistoryItems(c1.getString(c1.getColumnIndex(mDb.ORDER_REFNO)),
                        c1.getString(c1.getColumnIndex(mDb.ENTRYDATE)), c1.getString(c1.getColumnIndex(mDb.ORDER_TOTAL_AMOUNT))
                ));
                noOfOrders++;
            }

        if(noOfOrders == 0)
            _noOfOrders.setText("You have not placed any orders");
        else
            _noOfOrders.setText("You have placed "+noOfOrders +" orders");



    }

    @Override
    public void onClick(View v) {

    }
}
