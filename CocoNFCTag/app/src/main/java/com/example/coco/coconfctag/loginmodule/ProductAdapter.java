package com.example.coco.coconfctag.loginmodule;
/**
 * Created by cocoadmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;

import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolders> {

    private ArrayList<ProductItem> productList;
    private Context context;
   

    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productId,productPrice;

      

        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            productId = (TextView) view.findViewById(R.id.id_txt);
            productPrice = (TextView) view.findViewById(R.id.price_txt);
           

        }
    }


    public ProductAdapter(Context c, ArrayList<ProductItem> list) {
        this.productList = list;
        this.context = c;
    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_product_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {
        holder.productName.setText(productList.get(position).getProductName());
        holder.productId.setText(productList.get(position).getProductId());
        holder.productPrice.setText("$ "+productList.get(position).getProductPrice());
          }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}