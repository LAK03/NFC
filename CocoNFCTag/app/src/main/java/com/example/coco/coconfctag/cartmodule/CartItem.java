package com.example.coco.coconfctag.cartmodule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cocoadmin on 3/20/2017.
 */

public class CartItem implements Parcelable {
    private String productId="";
    private String productName="";
    private int productPrice=0;
    private int count=0;
    private int scantype=0;
    private boolean isChecked=false;

    public CartItem(String productId, String productName, int productPrice, int count, int scantype, boolean isChecked) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
        this.scantype = scantype;
        this.isChecked = isChecked;
    }


    protected CartItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        productPrice = in.readInt();
        count = in.readInt();
        scantype = in.readInt();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeInt(productPrice);
        dest.writeInt(count);
        dest.writeInt(scantype);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public CartItem() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getScantype() {
        return scantype;
    }

    public void setScantype(int scantype) {
        this.scantype = scantype;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
