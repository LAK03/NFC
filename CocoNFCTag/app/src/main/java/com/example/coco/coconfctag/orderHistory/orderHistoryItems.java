package com.example.coco.coconfctag.orderHistory;

import java.util.ArrayList;

/**
 * Created by Guest1 on 5/24/2017.
 */


class orderedProducts
{


    private String productId="";
    private String productName="";
    private int productPrice=0;
    private int count=0;

    public orderedProducts(String productId, String productName, int productPrice, int count) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}





public class orderHistoryItems {

    private String refNo ="";
    private String orderDate ="";
    private String ordertotal ="";

   // ArrayList<orderedProducts> arrList = new ArrayList<orderedProducts>();



    public orderHistoryItems(String refNo,String orderDate,String ordertotal) {
        this.refNo = refNo;

        this.orderDate =orderDate;
        this.ordertotal =ordertotal;

    }




    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }


    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrdertotal() {
        return ordertotal;
    }

    public void setOrdertotal(String ordertotal) {
        this.ordertotal = ordertotal;
    }


}

