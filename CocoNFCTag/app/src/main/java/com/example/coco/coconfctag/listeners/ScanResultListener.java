package com.example.coco.coconfctag.listeners;

import org.json.JSONObject;

/**
 * Created by cocoadmin on 4/7/2017.
 */

public interface ScanResultListener {
    void onScanResult(JSONObject obj,int scantype);
}
