package com.example.coco.coconfctag.database;

/**
 * Created by cocoadmin on 3/17/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.coco.coconfctag.loginmodule.UserItem;
import com.example.coco.coconfctag.readermodule.ProductItem;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NFCManager";
    private static final String TABLE_USER = "usertable";
    private static final String TABLE_PRODUCT = "producttable";
    private String USER_NAME = "username";
    private String USER_PASSWORD = "userpassword";
    private String ID = "id";
    private String PRODUCT_NAME = "productname";
    private String PRODUCT_ID = "productid";
    private String PRODUCT_PRICE = "productprice";
    private String TAG = "DatabaseHandler";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USER_NAME + " TEXT," + USER_PASSWORD + " TEXT " + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PRODUCT_ID + " TEXT ," + PRODUCT_NAME + " TEXT ," + PRODUCT_PRICE + " INTEGER " + ")";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public void addUser(UserItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, item.getUserName());
        values.put(USER_PASSWORD, item.getPassword());
        db.insert(TABLE_USER, null, values);
        db.close();
    }


    public void addProduct(ProductItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, item.getProductId());
        values.put(PRODUCT_NAME, item.getProductName());
        values.put(PRODUCT_PRICE, item.getProductPrice());
        db.insert(TABLE_PRODUCT, null, values);
        db.close();
    }


    public ArrayList<UserItem> getAllUsers() {
        ArrayList<UserItem> list = new ArrayList<UserItem>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                UserItem item = new UserItem();
                item.setId("" + cursor.getInt(0));
                item.setUserName(cursor.getString(1));
                item.setPassword(cursor.getString(2));
                list.add(item);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public UserItem getUserItem(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER + " where " + USER_NAME + " =? ", new String[]{username});
        ;
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    UserItem item = new UserItem();
                    item.setId("" + cursor.getInt(0));
                    item.setUserName(cursor.getString(1));
                    item.setPassword(cursor.getString(2));
                    return item;
                } while (cursor.moveToNext());
            }
        } else {
            return null;
        }
        return null;
    }

    public ProductItem getProductItem(String productid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_PRODUCT + " where " + PRODUCT_ID + " =? ", new String[]{productid});
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ProductItem item = new ProductItem();
                    item.setProductId("" + cursor.getString(1));
                    item.setProductName(cursor.getString(2));
                    item.setProductPrice(cursor.getInt(3));
                    return item;
                } while (cursor.moveToNext());
            }
        } else {
            return null;
        }
        return null;
    }

    public void updateUser(UserItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, item.getUserName());
        values.put(USER_PASSWORD, item.getPassword());
        db.update(TABLE_USER, values, ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }


}