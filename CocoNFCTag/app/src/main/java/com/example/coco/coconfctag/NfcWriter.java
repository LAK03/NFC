package com.example.coco.coconfctag;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;



public class NfcWriter extends AppCompatActivity {

    private NFCManager nfcMger;
    public static ArrayList<String> productDetails = new ArrayList<String>();
    private View v;
    private NdefMessage message = null;
    private ProgressDialog dialog;
    private Tag currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_writer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nfcMger = new NFCManager(this);
        v = findViewById(R.id.mainLyt);
        final Spinner sp = (Spinner) findViewById(R.id.tagType);
        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.tagContentType, android.R.layout.simple_spinner_dropdown_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);
        final EditText et1 = (EditText) findViewById(R.id.productId);
        final EditText et2 = (EditText) findViewById(R.id.productName);
        final EditText et3 = (EditText) findViewById(R.id.productPrice);
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = sp.getSelectedItemPosition();
                String productId = et1.getText().toString();
                String productName = et2.getText().toString();
                String productPrice = et3.getText().toString();
                String content = "Product Id : "+ productId + "\n" +"Product Name : "+productName + "\n" +"Product Price : "+productPrice;
                // productDetails.add(content);
                JSONObject jobj=new JSONObject();
                try {
                    jobj.put("id",productId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (pos) {
                    case 0:
                        message =  nfcMger.createUriMessage(content, "http://");
                        break;
                    case 1:
                        message =  nfcMger.createUriMessage(content, "tel:");
                        break;
                    case 2:
                        message =  nfcMger.createTextMessage(jobj.toString());
                        break;
                }
                if (message != null) {
                    dialog = new ProgressDialog(NfcWriter.this);
                    dialog.setMessage("Tag NFC Tag please");
                    dialog.show();;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            nfcMger.verifyNFC();
            //nfcMger.enableDispatch();
            Intent nfcIntent = new Intent(this, getClass());
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
            IntentFilter[] intentFiltersArray = new IntentFilter[] {};
            String[][] techList = new String[][] { { android.nfc.tech.Ndef.class.getName() }, { android.nfc.tech.NdefFormatable.class.getName() } };
            NfcAdapter nfcAdpt = NfcAdapter.getDefaultAdapter(this);
            nfcAdpt.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        }
        catch(NFCManager.NFCNotSupported nfcnsup) {
            Snackbar.make(v, "NFC not supported", Snackbar.LENGTH_LONG).show();
        }
        catch(NFCManager.NFCNotEnabled nfcnEn) {
            Snackbar.make(v, "NFC Not enabled", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        nfcMger.disableDispatch();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("Nfc", "New intent");
        // It is the time to write the tag
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (message != null) {
            nfcMger.writeTag(currentTag, message);
            dialog.dismiss();
            Snackbar.make(v, "Tag written", Snackbar.LENGTH_LONG).show();
        }
        else {
            // Handle intent
        }
    }
}
