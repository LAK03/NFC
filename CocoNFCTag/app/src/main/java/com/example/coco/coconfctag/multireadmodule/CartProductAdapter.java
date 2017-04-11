package com.example.coco.coconfctag.multireadmodule;
/**
 * Created by cocoadmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.readermodule.ProductItem;

import java.util.ArrayList;


public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.MyViewHolders> {

    private QuantityListener quantityListener;
    private ArrayList<ProductItem> productList;
    private Context context;


    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productPrices, totalPrices, count;
        public ImageView plusBtn, minusBtn;


        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            //productPrice = (TextView) view.findViewById(R.id.price_txt);
            //totalPrice = (TextView) view.findViewById(R.id.total_txt);
            count = (TextView) view.findViewById(R.id.count_txt);
            plusBtn = (ImageView) view.findViewById(R.id.plus_btn);
            minusBtn = (ImageView) view.findViewById(R.id.minus_btn);


        }
    }


    public CartProductAdapter(Context c, ArrayList<ProductItem> list, QuantityListener listener) {
        this.productList = list;
        this.context = c;
        this.quantityListener = listener;
    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_cartproduct_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {
        holder.productName.setText(productList.get(position).getProductName());
        holder.count.setText("" + productList.get(position).getCount());
//        holder.productPrice.setText("$ " + productList.get(position).getProductPrice());
//        holder.totalPrice.setText("Total " + (productList.get(position).getCount() * productList.get(position).getProductPrice()));
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityListener.onQuantityChange(productList.get(position).getProductId(), 1);
            }
        });
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productList.get(position).getCount() > 1)
                    quantityListener.onQuantityChange(productList.get(position).getProductId(), -1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}