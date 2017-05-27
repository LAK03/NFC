package com.example.coco.coconfctag.wishlistmodule;
/**
 * Created by cocoadmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.listeners.CheckboxListener;
import com.example.coco.coconfctag.listeners.WishlistListener;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;

import java.util.ArrayList;


public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolders> {


    private CheckboxListener checkboxListener;
    private WishlistListener wishlistListener;

    private ArrayList<ProductItem> productList;

    private Context context;

    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productPrice ;
        public ImageView  removeBtn;
        public CheckBox checkBox;



        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            productPrice = (TextView) view.findViewById(R.id.price_txt);

            removeBtn = (ImageView) view.findViewById(R.id.remove_btn);
            checkBox = (CheckBox) view.findViewById(R.id.chk_box);


        }
    }


    public WishlistAdapter(Context c, ArrayList<ProductItem> list,  WishlistListener wishlistLis,CheckboxListener checkListener) {
        this.productList = list;
        this.context = c;

        this.wishlistListener = wishlistLis;
        this.checkboxListener =checkListener;


    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_wishlist_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {

        holder.productName.setText(productList.get(position).getProductId() + " - " + productList.get(position).getProductName());

        holder.productPrice.setText("$ " + productList.get(position).getProductPrice());



        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlistListener.onFavouriteClicked(productList.get(position).getProductId(),false);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxListener.onChecked(productList.get(position).getProductId(),isChecked);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}