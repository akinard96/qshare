package com.fourdudes.qshare.Scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fourdudes.qshare.MainActivity
import com.fourdudes.qshare.R
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

private const val KEY_QR_CODE = "qr_code"
private const val LOG_TAG = "448.ScanActivity"
private const val REQUIRED_CAMERA_PERMISSION = android.Manifest.permission.CAMERA

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        setContentView(R.layout.activity_scan)

        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        // Programmatically initialize the scanner view
        mScannerView = ZXingScannerView(this)
        // Set the scanner view as the content view
        contentFrame.addView(mScannerView)
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause() called")
        // Stop camera on pause
        mScannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        Log.d(LOG_TAG, "Handling Result --->")

        // Prints scan results
        Log.d(LOG_TAG, rawResult.text)

        // Prints the scan format (qrcode, pdf417 etc.)
        Log.d(LOG_TAG, rawResult.barcodeFormat.toString())

        // Intent to MainActivity, extra is link right now
        /** TODO: Could be modified to pass file name, desc
         *  Insert as extras to decipher upon ListItemFragment creation in main
         *  Link is rawResult.text
         */
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEY_QR_CODE, rawResult.text)
        setResult(Activity.RESULT_OK, intent)
        startActivity(intent)
    }


}
