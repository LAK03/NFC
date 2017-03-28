package com.example.coco.coconfctag.readermodule;

/**
 * Created by cocoadmin on 3/20/2017.
 */

public class ProductItem {
    private String productId="";
    private String productName="";
    private int productPrice=0;
    private int count=0;

    public ProductItem(String productId, String productName, int productPrice, int count) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
    }

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
}
