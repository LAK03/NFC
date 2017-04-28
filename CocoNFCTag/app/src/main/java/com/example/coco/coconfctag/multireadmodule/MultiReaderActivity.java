package com.example.coco.coconfctag.multireadmodule;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coco.coconfctag.R;
import com.example.coco.coconfctag.barcode.BarcodeCaptureActivity;
import com.example.coco.coconfctag.database.DatabaseHandler;
import com.example.coco.coconfctag.listeners.IndividualItemListener;
import com.example.coco.coconfctag.listeners.QuantityListener;
import com.example.coco.coconfctag.scanlistmodule.ProductItem;
import com.example.coco.coconfctag.scanlistmodule.ScanListAdapter;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class MultiReaderActivity extends AppCompatActivity implements View.OnClickListener, QuantityListener, IndividualItemListener {

    private RecyclerView mProductRView;
    private LinearLayoutManager mLManager;
    private ArrayList<ProductItem> mProductArray = new ArrayList<>();
    private ScanListAdapter mScanListAdapter;
    private Spinner mDropdown;
    private String[] items;
    private String TAG = "MultiReaderActivity";
    private TextView mReadTxt;
    private int flagPos = 0;
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private DatabaseHandler mDB;
    private TextView mProductCount, mGrandTotal;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter mNfcAdapter;
    private Toolbar toolbar;
    private RelativeLayout mRLayout;
    private Animation animSlide;
    private TextView mMakePayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_read);
        init();
        setListeners();
    }

    private void setListeners() {
        mReadTxt.setOnClickListener(this);
        mMakePayment.setOnClickListener(this);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
        mReadTxt = (TextView) findViewById(R.id.read_txt);
        mProductRView = (RecyclerView) findViewById(R.id.product_rview);
        mProductCount = (TextView) findViewById(R.id.total_count);
        mGrandTotal = (TextView) findViewById(R.id.grand_total);
        mMakePayment = (TextView) findViewById(R.id.make_payment);
        mLManager = new LinearLayoutManager(this);
        mProductRView.setLayoutManager(mLManager);
        mScanListAdapter = new ScanListAdapter(this, mProductArray, this,this);
        mProductRView.setAdapter(mScanListAdapter);
        mDropdown = (Spinner) findViewById(R.id.spinner);
        mRLayout = (RelativeLayout) findViewById(R.id.rlayout);
        mDB = new DatabaseHandler(this);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        items = new String[]{"NFC", "Bar Code", "QR Code"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mDropdown.setAdapter(adapter);
        mDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "=" + position);
                mReadTxt.setText("Read " + items[position]);
                flagPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_txt:
                doRead();
                break;
            case R.id.make_payment:
                Toast.makeText(getApplicationContext(), "Processing Payment", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void doRead() {
        switch (flagPos) {
            case 0:
                break;

            case 1:
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                break;

            case 2:
                Intent intents = new Intent(this, BarcodeCaptureActivity.class);
                startActivityForResult(intents, BARCODE_READER_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(barcode.displayValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (obj != null)
                        addToCart(obj);

                } else Log.e(TAG, String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            } else super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addToCart(JSONObject obj) {


        String id = obj.optString("id");
        ProductItem dbItem = mDB.getProductItem(id);
        if (dbItem != null) {
            if (mProductArray.size() > 0) {
                ProductItem item = null;
                for (int i = 0; i < mProductArray.size(); i++) {
                    if (mProductArray.get(i).getProductId().equals(id)) {
                        item = mProductArray.get(i);
                        int count = item.getCount();
                        item.setCount(count + 1);
                    }
                }
                if (item == null)
                    mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductPrice(), 1,0,false));
            } else {
                mProductArray.add(new ProductItem(id, dbItem.getProductName(), dbItem.getProductPrice(), 1,0,false));
            }
            mScanListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Item not found on Database", Toast.LENGTH_SHORT).show();
        }
        changeCount();
    }

    private void changeCount() {
        int mCount = 0;
        int Total = 0;
        for (int i = 0; i < mProductArray.size(); i++) {
            mCount = mCount + mProductArray.get(i).getCount();
            Total = Total + (mProductArray.get(i).getCount() * mProductArray.get(i).getProductPrice());
        }
        mProductCount.setText("" + mCount);
        mGrandTotal.setText("Grand Total  $ " + Total);
        animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);

        animSlide.setRepeatCount(3);
// Start the animation like this
        mRLayout.startAnimation(animSlide);
    }


    @Override
    public void onQuantityChange(String id, int quantity) {
        for (int i = 0; i < mProductArray.size(); i++) {
            if (mProductArray.get(i).getProductId().equals(id)) {
                int count = mProductArray.get(i).getCount();
                mProductArray.get(i).setCount(count + quantity);
                mScanListAdapter.notifyDataSetChanged();
            }
        }
        changeCount();
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        if (mNfcAdapter != null)
            setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if (mNfcAdapter != null)
            stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);

    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {

        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public void OnCardClick(String productid) {

    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author munik
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {

                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jobj = new JSONObject(result);
                addToCart(jobj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
