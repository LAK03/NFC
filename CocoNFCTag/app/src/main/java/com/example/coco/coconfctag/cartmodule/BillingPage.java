package com.example.coco.coconfctag.cartmodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by Srikanth on 4/5/2017.
 */

public class BillingPage extends AppCompatActivity  {

    EditText _cardNumber;
    EditText _month;
    EditText _year;
    EditText _cardCode;
    EditText _amount;
    Button _pay;
    Toolbar _pageTitle;
    TextView _amountDisplay;
    TextView _txtView;
    TextView _txtQt;
    TextView _txtViewamount;

    String status="";
    String JsonResponse = null;
    String returnCode =null;
    int checkoutAmount=0;

    String CardNumber;
    String ExpiratonDate;
    String CVV;
    String refId;

    private DatabaseHandler mDB;


    Random ran = new Random();
    ArrayList<CartItem> itemDetails;


    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);




       _cardNumber = (EditText)findViewById(R.id.creditCardNo);
        _month =(EditText)findViewById(R.id.mon);
        _year = (EditText)findViewById(R.id.year);
        _cardCode =(EditText) findViewById(R.id.cardCode);

        _amountDisplay = (TextView)findViewById(R.id.tranAmount);

        _txtView = (TextView)findViewById(R.id.txtView);
        _txtQt = (TextView)findViewById(R.id.txtViewQt);
        _txtViewamount =(TextView)findViewById(R.id.txtViewAmount);

        mDB = new DatabaseHandler(getApplicationContext());

        _pageTitle = (Toolbar)findViewById(R.id.transactionPage_toolBar);
        setSupportActionBar(_pageTitle);
        getSupportActionBar().setTitle("Payment Page");

        Intent i =getIntent();
        checkoutAmount = i.getIntExtra("Checkout Amount",0);
        Bundle args = i.getBundleExtra("BUNDLE");
        itemDetails = (ArrayList<CartItem>) args.getSerializable("ARRAYLIST");

        Log.i("Item Name",itemDetails.get(0).getProductName());

        String textView = "Item Name\n";
        textView = textView +"-----------------------------\n";
        String textViewQuantity= "Quantity\n";
        textViewQuantity = textViewQuantity + "-----------------\n";
        String textViewAmount = "Price\n";
        textViewAmount = textViewAmount +"---------------\n";



         for(int j = 0;j<itemDetails.size();j++)
         {
             textView = textView + itemDetails.get(j).getProductName() + "\n";
             textViewQuantity = textViewQuantity + itemDetails.get(j).getCount() +"\n";
             textViewAmount = textViewAmount +"$"+itemDetails.get(j).getProductPrice() +"\n";

         }



        _txtView.setText(textView);
        _txtQt.setText(textViewQuantity);
        _txtViewamount.setText(textViewAmount);




        _amountDisplay.setText("subtotal :                                  $"+String.valueOf(checkoutAmount));

        _pay =(Button)findViewById(R.id.pay);




        _pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 String mon = _month.getText().toString().trim();
                 String year = _year.getText().toString().trim();
                 CardNumber = _cardNumber.getText().toString().trim();
                 ExpiratonDate = mon + year;
                 CVV = _cardCode.getText().toString().trim();



                if(CardNumber.isEmpty() || mon.isEmpty() ||  year.isEmpty() || CVV.isEmpty()) {
                    if(CardNumber.isEmpty())
                        _cardNumber.setError("Card Number cannot be empty");
                     if(mon.isEmpty())
                        _month.setError("Expiration month cannot be empty");
                     if(year.isEmpty())
                        _year.setError("Expiration year cannot be empty");
                     if(CVV.isEmpty())
                        _cardCode.setError("Card code cannot be empty");
                }
                else
                {
                    new AlertDialog.Builder(BillingPage.this)
                            .setTitle("Transaction Confirmation Page")
                            .setMessage("Are you sure you want to confirm ?")
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendJSONDataToServer task = new sendJSONDataToServer();
                                    try {
                                        task.execute().get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                    if (status.contains("Successful.")) {

                                        Toast.makeText(getApplicationContext(), "Transaction Successful", Toast.LENGTH_SHORT).show();
                                        storeOrderHistory();
                                        emptyCheckoutData();
                                        onPressGotoHomePage();
                                    }  if (status.contains("cardNumber") || status.contains("credit card number is invalid")) {
                                        _cardNumber.setError("Invalid Card Number");

                                    } if(status.contains("Expiration")) {
                                        _month.setError("Invalid expiration date");
                                        _year.setError("Invalid Expiration Date");
                                    }

                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();



                }

            }
        });

    }

    void storeOrderHistory()
    {
        for(int i =0 ;i<itemDetails.size();i++)
            mDB.insertEntry(refId,itemDetails.get(i).getProductId(),itemDetails.get(i).getProductName(),itemDetails.get(i).getProductPrice(),itemDetails.get(i).getCount(),
                    String.valueOf(checkoutAmount));
    }
    void onPressGotoHomePage()
    {
        Intent i = new Intent(BillingPage.this,com.example.coco.coconfctag.loginmodule.LoginActivity.class);
        startActivity(i);
    }
    void emptyCheckoutData()
    {
        itemDetails.clear();
        prefs = getSharedPreferences("cocosoft", MODE_PRIVATE);
        String beforetempdata = prefs.getString("tempcartlist",null);
        Log.i("beforetempdata",beforetempdata);
        prefsEditor = prefs.edit();
        String json = (new Gson()).toJson(itemDetails);
        prefsEditor.putString("tempcartlist", json);
        prefsEditor.commit();

        String aftertempdata = prefs.getString("tempcartlist",null);
        Log.i("aftertempdata",aftertempdata);


    }

    public class sendJSONDataToServer extends AsyncTask<String, Void, String>
    {
        JSONObject jPaymentDetails = storeDataInJson();
        String json = jPaymentDetails.toString();

        String statusOp;
        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("https://apitest.authorize.net/xml/v1/request.api");
                HttpURLConnection con;
                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                Writer writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
                writer.write(json);
                Log.i("JSON String",json);
                writer.close();

                InputStream inputStream = con.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");

                JsonResponse = buffer.toString();
                Log.i("JSON Response",JsonResponse);
                storeJSONDataInDB(JsonResponse);
                con.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }

    public void storeJSONDataInDB(String JsonResponse)
    {
        String desc =null;
        try {
            JSONObject  jsonObj = new JSONObject(JsonResponse);

            JSONArray jsonStatusArray= jsonObj.getJSONObject("messages").getJSONArray("message");




            for (int i = 0; i < jsonStatusArray.length(); ++i) {
                JSONObject rec = jsonStatusArray.getJSONObject(i);
                returnCode = rec.getString("code");
                status = rec.getString("text");
                Log.i("code",returnCode);
                Log.i("status",status);
            }

            Log.i("returnCode",returnCode);

            if(jsonObj.getJSONObject("transactionResponse").has("messages")) {
                Log.i("Inside","transactionResponse");
                    Log.i("Inside","messages");

                    desc = jsonObj.getJSONObject("transactionResponse").getJSONArray("messages").getJSONObject(0).getString("description");
                    status = status + desc;
            }
            else
            {
                Log.i("Inside","errorText");
                desc = jsonObj.getJSONObject("transactionResponse").getJSONArray("errors").getJSONObject(0).getString("errorText");
                status = status + desc;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject storeDataInJson()
    {

        String amount = String.valueOf(checkoutAmount);



        refId =  String.valueOf((100000 + ran.nextInt(899999)));

        JSONObject jfinal = new JSONObject();
        JSONObject jkeys =new JSONObject();
        JSONObject jCreditCardTrans = new JSONObject();

        JSONObject jTransactionReq = new JSONObject();
        JSONObject jInfo = new JSONObject();
        JSONObject jObjectType = new JSONObject();
        JSONObject jObjectTax = new JSONObject();
        JSONObject jObjectduty = new JSONObject();

        try {
            jkeys.put("name","7vz8xGg3D7j");
            jkeys.put("transactionKey","4brhB8ukY2R8n98W");
            jCreditCardTrans.put("merchantAuthentication",jkeys);
            jCreditCardTrans.put("refId",refId);
            jTransactionReq.put("transactionType" ,"authCaptureTransaction");
            jTransactionReq.put("amount",amount);
            jObjectType.put("cardNumber",CardNumber);
            jObjectType.put("expirationDate",ExpiratonDate);
            jObjectType.put("cardCode",CVV);
            jInfo.put("creditCard",jObjectType);
            jTransactionReq.put("payment",jInfo);

            JSONObject itemS =new JSONObject();
            for(int i = 0 ; i < itemDetails.size() ; i++) {

                JSONObject json =new JSONObject();
                json.put("itemId",i);
                json.put("name",itemDetails.get(i).getProductName());
                json.put("description","");
                json.put("quantity",itemDetails.get(i).getCount());
                json.put("unitPrice",itemDetails.get(i).getProductPrice());
                itemS.accumulate("lineItem",json);

            }
            jTransactionReq.put("lineItems",itemS);
            jObjectTax.put("amount","4.26");
            jObjectTax.put("name","level2 tax name");
            jObjectTax.put("description","level2 tax");
            jTransactionReq.put("tax",jObjectTax);
            jObjectduty.put("amount","8.55");
            jObjectduty.put("name","duty name");
            jObjectduty.put("description","duty description");
            jTransactionReq.put("duty",jObjectduty);
            jCreditCardTrans.put("transactionRequest",jTransactionReq);
            jfinal.put("createTransactionRequest",jCreditCardTrans);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON String",jfinal.toString());
        return jfinal;
    }
}
