package com.example.coco.coconfctag;


import android.nfc.FormatException;
import android.nfc.tech.NdefFormatable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by user on 8/8/16.
 */
public class Tab3 extends Fragment {
    static final String TAG = "Cocosoft demo";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.tab3, container, false);
    }

    /*public static interface NfcActionCallbackPicker {
        public NfcActionCallback getNfcActionCallback();
    }

    public interface NfcActionCallback {

        public void onNfcNotSupported();

        public void onNfcDisabled();

        public void onTagArrival(NdefBuilderBase builder);

        public void onTagWriteCompleted();

        public void onTagWriteFailure();
    }

    public interface NdefBuilder {
        public Integer getSize();

        public boolean isLock();

        public void write(NdefMessage ndefMessage);

        public void lock();

        public Tag getTag();
    }


    final Charset UTF8 = Charset.forName("UTF-8");

    NfcAdapter mNfcAdapter;
    NfcActionCallback mCallback;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        NfcActionCallbackPicker picker = (NfcActionCallbackPicker) activity;
        mCallback = picker.getNfcActionCallback();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            mCallback.onNfcNotSupported();
        } else if (mNfcAdapter.isEnabled() == false) {
            mCallback.onNfcDisabled();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // NfcAdapter#enableForegroundDispatch を設定
        enableForegroundDispatch();
    }

    public void enableForegroundDispatch() {
        final Activity activity = getActivity();
        IntentFilter[] filters = makeFilter();
        String[][] techLists = makeTechLists();
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0,
                new Intent(activity, activity.getClass()), 0);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(activity, pendingIntent,
                    filters, techLists);
        }
    }
    IntentFilter[] makeFilter() {
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tech, tech };
        return filters;
    }

    String[][] makeTechLists() {
        String[] ndef = new String[] { Ndef.class.getName() };
        String[] ndefFormatable = new String[] { NdefFormatable.class.getName() };
        String[][] techLists = new String[][] { ndef, ndefFormatable };
        return techLists;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(getActivity());
        }
    }

    public void onNewIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeNdefMessage(tag);
        }
    }


    void writeNdefMessage(Tag tag) {
        try {
            if (Arrays.asList(tag.getTechList()).contains(
                    NdefFormatable.class.getName())) {


                NdefFormatable ndef = NdefFormatable.get(tag);
                NdefBuilderForNdefFormatable builder = new NdefBuilderForNdefFormatable(
                        ndef);
                mCallback.onTagArrival(builder);
                if (builder.mNdefMessage == null) {
                    ndef.close();
                    return;
                }

                try {

                    if (!ndef.isConnected()) {
                        ndef.connect();
                    }

                    if (builder.doLock) {
                        ndef.formatReadOnly(builder.mNdefMessage);
                    } else {
                        ndef.format(builder.mNdefMessage);
                    }

                    mCallback.onTagWriteCompleted();
                } catch (IOException e) {
                    Log.d(TAG, "NdefFormatable IO Exception", e);
                    ndef.close();

                    if (!ndef.isConnected()) {
                        ndef.connect();
                    }

                    NdefRecord ndefRecord = NfcUtil.newTextRecord(
                            "AppTagMaker", Locale.US, true);
                    NdefMessage ndefMessage = new NdefMessage(
                            new NdefRecord[]{ndefRecord});

                    ndef.format(ndefMessage);

                    Toast.makeText(getActivity(),
                            R.string.tag_write_error_undefined,
                            Toast.LENGTH_SHORT).show();
                } finally {
                    ndef.close();
                }
            } else if (Arrays.asList(tag.getTechList()).contains(
                    Ndef.class.getName())) {


                Ndef ndef = Ndef.get(tag);
                NdefBuilderForNdef builder = new NdefBuilderForNdef(ndef);
                mCallback.onTagArrival(builder);
                if (builder.mNdefMessage == null) {
                    ndef.close();
                    return;
                }

                try {

                    if (!ndef.isConnected()) {
                        ndef.connect();
                    }

                    if (ndef.isWritable()) {

                        ndef.writeNdefMessage(builder.mNdefMessage);
                        if (builder.doLock) {
                            ndef.makeReadOnly();
                        }

                        mCallback.onTagWriteCompleted();
                    }
                } finally {
                    ndef.close();
                }
            }
        } catch (FormatException e) {
            Log.e(this.getClass().getSimpleName(), "FormatException", e);
            mCallback.onTagWriteFailure();
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "IOException", e);
            mCallback.onTagWriteFailure();
        }
    }


    public static abstract class NdefBuilderBase implements NdefBuilder {

        Tag mTag;

        NdefMessage mNdefMessage;
        boolean doLock = false;

        NdefBuilderBase(Tag tag) {
            mTag = tag;
        }

        @Override
        public final void write(NdefMessage ndefMessage) {
            mNdefMessage = ndefMessage;
        }

        @Override
        public final void lock() {
            doLock = true;
        }

        @Override
        public final Tag getTag() {
            return mTag;
        }
    }

    public static class NdefBuilderForNdef extends NdefBuilderBase {

        Ndef mNdef;

        NdefBuilderForNdef(Ndef ndef) {
            super(ndef.getTag());
            mNdef = ndef;
        }

        @Override
        public Integer getSize() {
            return mNdef.getMaxSize();
        }

        @Override
        public boolean isLock() {
            return !mNdef.isWritable();
        }
    }

    public static class NdefBuilderForNdefFormatable extends NdefBuilderBase {

        NdefFormatable mNdef;

        NdefBuilderForNdefFormatable(NdefFormatable ndef) {
            super(ndef.getTag());
            mNdef = ndef;
        }

        @Override
        public Integer getSize() {
            return null;
        }

        @Override
        public boolean isLock() {
            return false;
        }
    }

    public static class NdefBuilderUnsupported extends NdefBuilderBase {

        NdefBuilderUnsupported(Tag tag) {
            super(tag);
        }

        @Override
        public Integer getSize() {
            return null;
        }

        @Override
        public boolean isLock() {
            return true;
        }
    }
*/



}
