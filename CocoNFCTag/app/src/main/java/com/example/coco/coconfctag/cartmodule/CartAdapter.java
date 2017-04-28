package com.example.coco.coconfctag.cartmodule;
/**
 * Created by cocoadmin on 3/20/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.CheckboxListener;
import com.example.coco.coconfctag.listeners.IndividualItemListener;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.listeners.WishlistListener;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolders> {

    private CheckboxListener checkboxListener;
    private IndividualItemListener individualListener;
    private boolean isloggedin;
    private SharedPreferences prefs;
    private WishlistListener wishlistListener;
    private QuantityListener quantityListener;
    private ArrayList<CartItem> productList;
    private DatabaseHandler mDB;
    private Context context;

    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, count;
        public ImageView plusBtn, minusBtn, indicatorImg,removeBtn;
        public CheckBox favBtn,cartCheckbox;
        public CardView cardView;


        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name_txt);
            productPrice = (TextView) view.findViewById(R.id.price_txt);
            count = (TextView) view.findViewById(R.id.count_txt);
            plusBtn = (ImageView) view.findViewById(R.id.plus_btn);
            minusBtn = (ImageView) view.findViewById(R.id.minus_btn);
            removeBtn = (ImageView) view.findViewById(R.id.remove_btn);
            favBtn = (CheckBox) view.findViewById(R.id.fav_btn);
            cartCheckbox = (CheckBox) view.findViewById(R.id.cart_chkbox);
            indicatorImg = (ImageView) view.findViewById(R.id.indicator);
            cardView = (CardView) view.findViewById(R.id.card_view);

        }
    }


    public CartAdapter(Context c, ArrayList<CartItem> list, QuantityListener listener, WishlistListener wishlistLis, IndividualItemListener individualListener, CheckboxListener checkboxLis) {
        this.productList = list;
        this.context = c;
        this.quantityListener = listener;
        this.wishlistListener = wishlistLis;
        this.prefs = context.getSharedPreferences("cocosoft", MODE_PRIVATE);
        this.isloggedin = prefs.getBoolean("isloggedin", false);
        this.mDB = new DatabaseHandler(context);
        this.individualListener = individualListener;
        this.checkboxListener=checkboxLis;
    }

    public CartAdapter(Context c, ArrayList<CartItem> list, QuantityListener listener, IndividualItemListener individualListener) {
        this.productList = list;
        this.context = c;
        this.quantityListener = listener;
        this.prefs = context.getSharedPreferences("cocosoft", MODE_PRIVATE);
        this.isloggedin = prefs.getBoolean("isloggedin", false);
        this.mDB = new DatabaseHandler(context);
        this.individualListener = individualListener;

    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_cart_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {
        String username = prefs.getString("username", "");
        if (isloggedin) {
            if (mDB.wishlistAlreadyAdded(username, productList.get(position).getProductId()))
                holder.favBtn.setChecked(true);
        }
        holder.productName.setText(productList.get(position).getProductId() + " - " + productList.get(position).getProductName());
        holder.count.setText("" + productList.get(position).getCount());
        holder.productPrice.setText("$ " + productList.get(position).getProductPrice());
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

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityListener.onQuantityChange(productList.get(position).getProductId(), 0);
            }
        });
        switch (productList.get(position).getScantype()) {
            case 1:
                holder.indicatorImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_nfc));
                break;

            case 2:
                holder.indicatorImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_barcode));
                break;

            case 3:
                holder.indicatorImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_qrcode));
                break;
        }
        holder.favBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isloggedin) {
                    wishlistListener.onFavouriteClicked(productList.get(position).getProductId(), isChecked);

                } else {
                    Toast.makeText(context, "Please login to continue", Toast.LENGTH_SHORT).show();
                    holder.favBtn.setChecked(false);
                }

            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                individualListener.OnCardClick(productList.get(position).getProductId());
            }
        });
        holder.cartCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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