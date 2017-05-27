package com.example.coco.coconfctag.scanlistmodule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cocoadmin on 3/20/2017.
 */

public class ProductItem implements Parcelable {
    private String productId="";
    private String productName="";
    private int productPrice=0;
    private int count=0;
    private int scantype=0;
    private boolean isChecked=false;

    public ProductItem(String productId, String productName, int productPrice, int count, int scantype, boolean isChecked) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
        this.scantype = scantype;
        this.isChecked = isChecked;
    }


    protected ProductItem(Parcel in) {
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

    public static final Creator<ProductItem> CREATOR = new Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ProductItem() {
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
