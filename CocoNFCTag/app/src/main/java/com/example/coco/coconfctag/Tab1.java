package com.example.coco.coconfctag;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.nfc.NfcAdapter;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import com.example.coco.coconfctag.multireadmodule.MultiReaderActivity;
import com.example.coco.coconfctag.readermodule.ReaderActivity;


/**
 * Created by user on 8/8/16.
 */
public class Tab1 extends Fragment {

    public static final String TAG = "NFCReader Demo";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private TextView mTextView;

    private Button  mButton,mButtonWrite,mButtonMultiReader;

    //private NfcAdapter mNfcAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1, container, false);
        mTextView = (TextView) view.findViewById(R.id.textView_explanation);
        mButton =(Button) view.findViewById(R.id.btnReader);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ///startActivity(new Intent(getActivity(), NfcReader.class));
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                startActivity(intent);
            }
        });
        mButtonWrite =(Button) view.findViewById(R.id.btnWriter);
        mButtonWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ///startActivity(new Intent(getActivity(), NfcReader.class));
                Intent intent = new Intent(getActivity(), NfcWriter.class);
                startActivity(intent);
            }
        });
        mButtonMultiReader =(Button) view.findViewById(R.id.btnMultiTagReader);
        mButtonMultiReader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ///startActivity(new Intent(getActivity(), NfcReader.class));
                Intent intent = new Intent(getActivity(), MultiReaderActivity.class);
                startActivity(intent);

            }
        });


        //mTextView = (TextView) getView(R.id.textView_explanation);
        /*mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(getActivity(), "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            getActivity().finish();
            //return;
  }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            mTextView.setText(R.string.explanation);
        }

        handleIntent(getActivity().getIntent());*/



        return view;


    }


  /* private void handleIntent(Intent intent) {
        String action = intent.getAction();
        *//*if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else *//*
       if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
       {

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
    public void onResume() {
        super.onResume();

       *//* *//**//**//**//**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         *//**//**//**//**//*
        setupForegroundDispatch(getActivity(), mNfcAdapter);
    }

    @Override
    public void onPause() {
       *//* *//**//**//**//**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         *//**//**//**//**//*
        stopForegroundDispatch(getActivity(), mNfcAdapter);

        super.onPause();
    }

    public static String NFC_str;

    public void setNFC(String str)
    {
        NFC_str = str;
    }

    protected void onNewIntent(Intent intent) {
       *//* *//**//**//**//**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         *//**//**//**//**//*

        //handleIntent(intent);
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }

        else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
           *//* Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeNdefMessage(tag);*//*
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

        mTextView.setText(NFC_str);
    }

   *//* *//**//**//**//**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     *//**//**//**//**//*
    public void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {

       *//* FragmentManager manager = activity.getFragmentManager();
*//*
        final Activity activity1 = getActivity();

        final Intent intent = new Intent(activity1, activity1.getClass());

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity1, 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];

        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();

        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);

        filters[0].addCategory(Intent.CATEGORY_DEFAULT);

        try
        {
            filters[0].addDataType(MIME_TEXT_PLAIN);

        }
        catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity1, pendingIntent, filters, techList);
    }

  *//*  *//**//**//**//**//**//**//**//**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @pa*//**//*ram adapter The {@link NfcAdapter} used for the foreground dispatch.
     *//**//**//**//**//*
    public void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {

        final Activity activity1 = getActivity();

        adapter.disableForegroundDispatch(activity1);

    }

   *//* *//**//**//**//**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author munik
     *
     *//**//**//**//**//*
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
       *//* *//**//**//**//*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         *//**//**//**//**//*

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
            if (result != null) {
                mTextView.setText("Product Details: \n" + result);
            }
        }


    }*/
}
