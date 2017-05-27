package com.example.coco.coconfctag.orderHistory;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.loginmodule.ProductAdapter;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

/**
 * Created by Guest1 on 5/24/2017.
 */

public class orderHistoryAdaptor extends RecyclerView.Adapter<orderHistoryAdaptor.MyViewHolders> {

    public ArrayList<orderHistoryItems> historyList = new ArrayList<orderHistoryItems>();
    ArrayList<orderedProducts> productInfo = new ArrayList<orderedProducts>();
    private Context context;


    DatabaseHandler mDb;





    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, _orderdate, _orderSum, _orderNumber;
        public GridView _lView;

        public MyViewHolders(View view) {
            super(view);
            mDb = new DatabaseHandler(context);
            _orderdate = (TextView) view.findViewById(R.id.orderDate);
            _orderSum = (TextView) view.findViewById(R.id.orderSum);
            _orderNumber = (TextView) view.findViewById(R.id.orderNumber);

            _lView = (GridView) view.findViewById(R.id.adapter_item_list);


        }

    }

    public orderHistoryAdaptor(Context c, ArrayList<orderHistoryItems> list) {
        this.historyList = list;
        this.context = c;
    }

    @Override
    public orderHistoryAdaptor.MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_order_history, parent, false);

        return new orderHistoryAdaptor.MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final orderHistoryAdaptor.MyViewHolders holder, final int position) {




        holder._orderSum.setText("TOTAL \n" + "$" + historyList.get(position).getOrdertotal());
        holder._orderdate.setText("ORDER PLACED\n" + historyList.get(position).getOrderDate());

        holder._orderNumber.setText("ORDER # " + historyList.get(position).getRefNo());
        productInfo.clear();
        displayProductHistoryforOrderNumber(historyList.get(position).getRefNo());

        productHistoryAdaptor adapter = new productHistoryAdaptor(context, productInfo);

        Log.e("count", String.valueOf(adapter.getCount()));
        holder._lView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    void displayProductHistoryforOrderNumber(String orderNo) {
        Cursor c = mDb.getOrderHistory(orderNo);

        while (c.moveToNext()) {

            Log.d("Product ID", c.getString(c.getColumnIndex(mDb.PRODUCT_ID)));
            Log.d("product Name", c.getString(c.getColumnIndex(mDb.PRODUCT_NAME)));

            productInfo.add(new orderedProducts(c.getString(c.getColumnIndex(mDb.PRODUCT_ID)),
                    c.getString(c.getColumnIndex(mDb.PRODUCT_NAME)),
                    c.getInt(c.getColumnIndex(mDb.PRODUCT_PRICE)),
                    c.getInt(c.getColumnIndex(mDb.PRODUCT_COUNT))));


        }
    }
}

