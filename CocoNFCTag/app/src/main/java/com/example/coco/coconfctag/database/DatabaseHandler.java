package com.example.coco.coconfctag.database;

/**
 * Created by cocoadmin on 3/17/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.coco.coconfctag.loginmodule.UserItem;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NFCManager";
    private static final String TABLE_USER = "usertable";
    private static final String TABLE_PRODUCT = "producttable";
    private static final String TABLE_CART = "carttable";
    private static final String TABLE_WISHLIST = "wishlisttable";
    private static final String TABLE_ORDERHISTORY = "order_history";
    private String USER_NAME = "username";
    private String USER_PASSWORD = "userpassword";
    private String ID = "id";

    public String PRODUCT_NAME = "productname";
    public String PRODUCT_ID = "productid";
    public String PRODUCT_PRICE = "productprice";
    public String PRODUCT_COUNT = "productcount";
    public String TAG = "DatabaseHandler";

    public String ORDER_REFNO = "order_refNo";
    public String ENTRYDATE = "ORDER_DATE";
    public String ORDER_TOTAL_AMOUNT = "order_sum";



    private String CREATE_TABLE_ORDERHISTORY = "CREATE TABLE " +TABLE_ORDERHISTORY
            +"(" +ID+
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +ORDER_REFNO+
            " VARCHAR, " +PRODUCT_ID+
            " VARCHAR, " +PRODUCT_NAME+
            " VARCHAR, " +PRODUCT_PRICE+
            " INT, " +PRODUCT_COUNT+
            " INT, " +ENTRYDATE +
            " VARCHAR ,"+ ORDER_TOTAL_AMOUNT +
            " VARCHAR );";

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

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PRODUCT_ID + " TEXT ," + USER_NAME + " TEXT ," + PRODUCT_COUNT + " INTEGER " + ")";
        db.execSQL(CREATE_CART_TABLE);

        String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHLIST + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PRODUCT_ID + " TEXT ," + USER_NAME + " TEXT " + ")";
        db.execSQL(CREATE_WISHLIST_TABLE);


        Log.e("Creating the table",CREATE_TABLE_ORDERHISTORY);
        db.execSQL(CREATE_TABLE_ORDERHISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERHISTORY);
        Log.e("Dropping the table",TABLE_ORDERHISTORY);
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


    public void addToCart(ProductItem item, String username, int count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, item.getProductId());
        values.put(USER_NAME, username);
        values.put(PRODUCT_COUNT, count);
        db.insert(TABLE_CART, null, values);
        db.close();
    }

    public void addToWishlist(String productid, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, productid);
        values.put(USER_NAME, username);
        db.insert(TABLE_WISHLIST, null, values);
        db.close();
    }

    public boolean wishlistAlreadyAdded(String username, String productid) {

        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + TABLE_WISHLIST + " WHERE " + USER_NAME + " =? AND " + PRODUCT_ID + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[]{username, productid});

        boolean hasObject = false;
        if (cursor.moveToFirst()) {
            hasObject = true;

            //region if you had multiple records to check for, use this region.

            int count = 0;
            while (cursor.moveToNext()) {
                count++;
            }
            //here, count is records found
            Log.d(TAG, String.format("%d records found", count));

            //endregion

        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return hasObject;

    }

    public ArrayList<String> getAllWishList(String username) {
        ArrayList<String> wishlist = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_WISHLIST + " WHERE " + USER_NAME + " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{username});
        if (cursor.moveToFirst()) {
            do {

                wishlist.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return wishlist;
    }


    public ArrayList<HashMap<String, Integer>> getAllCartList(String username) {
        ArrayList<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();
        String selectQuery = "SELECT * FROM " + TABLE_CART + " WHERE " + USER_NAME + " = " + username;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Integer> hash = new HashMap<String, Integer>();
                hash.put(cursor.getString(1), cursor.getInt(3));
                list.add(hash);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public void removeWishlist(String productid, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WISHLIST,
                USER_NAME + "=? AND " + PRODUCT_ID + "=? ",
                new String[] {username, productid});
        db.close();
    }


    public void insertEntry(String refno,String product_id,String product_name, int price,int count,String ordersum)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMMM-dd");
        String date = sdf.format(new Date());

        Log.d("Date in database ",date);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(ORDER_REFNO, refno);
        newValues.put(PRODUCT_ID, product_id);
        newValues.put(PRODUCT_NAME,product_name);
        newValues.put(PRODUCT_PRICE, price);
        newValues.put(PRODUCT_COUNT,count);
        newValues.put(ENTRYDATE, date);
        newValues.put(ORDER_TOTAL_AMOUNT,ordersum);
        db.insert(TABLE_ORDERHISTORY, null, newValues);
        db.close();
    }

    public Cursor getOrderNumbers()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection ={"DISTINCT "+ORDER_REFNO ,ENTRYDATE,ORDER_TOTAL_AMOUNT};
        Cursor c = db.query(TABLE_ORDERHISTORY, projection, null, null, null, null, null);
        int i=c.getCount();
        Log.e("ORDER_REFNO count",String.valueOf(i));


        return c;
    }

    public Cursor getOrderHistory(String orderNo)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {ORDER_REFNO,PRODUCT_ID,PRODUCT_NAME,PRODUCT_PRICE,PRODUCT_COUNT,ENTRYDATE,ORDER_TOTAL_AMOUNT};
        String selection =  ORDER_REFNO+ " = ?";
        String[] selectionArgs = {orderNo};


        Cursor c = db.query(TABLE_ORDERHISTORY, projection, selection, selectionArgs, null, null, null);
        int i=c.getCount();
        return c;

    }
}