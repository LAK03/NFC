package com.example.coco.coconfctag.orderHistory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coco.coconfctag.R;

import java.util.ArrayList;

/**
 * Created by Guest1 on 5/25/2017.
 */

public class productHistoryAdaptor extends BaseAdapter {


    public ArrayList<orderedProducts> productsOrder = new ArrayList<orderedProducts>();
    private Context context;

    public productHistoryAdaptor(Context c, ArrayList<orderedProducts> productsOrder) {
       // Log.e("productHistoryAdaptor1",list.get(2).getProductName());
        Log.e("productHistory count",String.valueOf(productsOrder.size()));
        this.productsOrder = productsOrder;
        this.context = c;
    }
    @Override
    public int getCount() {
        return productsOrder.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Log.e("inside getView",String.valueOf(position));
        View gv_items = convertView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        //gv_items = new View(context);
        if (convertView == null) {
            Log.e("Inside convertView ", "null");

            gv_items = inflater.inflate(R.layout.inflate_products_history, null);
        }
        TextView productName = (TextView) gv_items.findViewById(R.id.productName);
        TextView productPrice = (TextView) gv_items.findViewById(R.id.productPrice);

            productName.setText(productsOrder.get(position).getProductName());
            productPrice.setText("$ " + productsOrder.get(position).getProductPrice());;

        return gv_items;
    }
}
