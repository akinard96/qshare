package com.fourdudes.qshare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fourdudes.qshare.AboutPage.AboutActivity
import com.fourdudes.qshare.HelpPage.HelpActivity
import com.fourdudes.qshare.Scan.ScanActivity
import com.fourdudes.qshare.Settings.SettingsActivity
import com.fourdudes.qshare.detail.ItemDetailFragment
import com.fourdudes.qshare.drive.DriveServiceHelper
import com.fourdudes.qshare.list.ItemListFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.leinardi.android.speeddial.SpeedDialView
import java.util.*

const val REQUEST_CODE_FILE = 0
const val REQUEST_CODE_SIGN_IN = 1
const val REQUEST_CODE_CAMERA_PERMISSION = 2
private const val KEY_QR_CODE = "qr_code"
private const val KEY_NEW_FILE = "new_file"
private const val KEY_FILE_NAME = "file_name"
private const val KEY_FILE_DESC = "file_desc"
private const val REQUIRED_CAMERA_PERMISSION = android.Manifest.permission.CAMERA

const val LOG_TAG = "4dudes.MainActivityLog"

class MainActivity : AppCompatActivity(), ItemListFragment.Callbacks {

    private lateinit var driveServiceHelper: DriveServiceHelper
    private lateinit var driveService: Drive
    private var signedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set current frag to Item list view
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = ItemListFragment()

            // Check for QR link
            val link: String? = intent.extras?.getString(KEY_QR_CODE)
            if ( link != null ) {
                // Coming from scanner
                Log.d("448.ScanActivity", "Extra received: $link")

                // Attach link to bundle
                val args = Bundle().apply {
                    putSerializable(KEY_QR_CODE, link)
                }
                fragment.apply {
                    arguments = args
                }
            }
            // Start transaction
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }


        //prompt the user to sign in via google drive
        requestSignIn()

        val speedDialView = findViewById<SpeedDialView>(R.id.speed_dial)
        speedDialView.inflate(R.menu.fab_actions_menu)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.upload_fab -> {
                    makeText(this,
                        "open android file selector",
                        LENGTH_SHORT
                    )
                    .show()

                    if(signedIn){
                        val chooserIntent = driveServiceHelper.createFilePickerIntent()
                        startActivityForResult(chooserIntent, REQUEST_CODE_FILE)
                    }
                    else {
                        Log.d(LOG_TAG, "User not signed in!")
                        //TODO present dialog explaining that the user must sign in
                    }

//                    intent.putExtra(CATEGORY_OPENABLE)
                    speedDialView.close()
                    true
                }
                R.id.scan_fab -> {
                    // Permissions
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if( ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_CAMERA_PERMISSION ) ) {
                            Log.d("camera_perm", "Showing user camera permission explanation")
                            makeText(baseContext,
                                R.string.camera_permission_rationale,
                                LENGTH_SHORT)
                                .show()
                            false
                        } else {
                            Log.d("camera_perm", "No explanation needed, just request camera permission")
                            requestPermissions(listOf(REQUIRED_CAMERA_PERMISSION).toTypedArray(), REQUEST_CODE_CAMERA_PERMISSION)
                            false
                        }
                    } else {
                        val intent = Intent(this, ScanActivity::class.java)
                        startActivity(intent)
                        speedDialView.close()
                        true
                    }
                }
                else -> {
                    false
                }
            }
        })
//        speedDialView.addActionItem(
//            SpeedDialActionItem.Builder(R.id.fab_no_label, R.drawable.ic_link_white_24dp)
//                .create())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK){
            return
        }
        else {
            if(requestCode == REQUEST_CODE_FILE){
                if(data == null){
                    return
                }
                val fileUri = data.data ?: return
                Log.d(LOG_TAG, "file received: $fileUri")
                openFileFromFilePicker(fileUri)
                uploadFileFromFilePicker(fileUri)
            }
            if(requestCode == REQUEST_CODE_SIGN_IN){
                if(data == null){
                    return
                }
                handleSignInResult(data)
            }
            if(requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("camera_perm", "Result ok, permission granted")
                    val intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about_menu_item -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.help_menu_item -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.settings_menu_item -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun requestSignIn() {
        Log.d(LOG_TAG, "Requesting sign in")

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)

        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun handleSignInResult(result: Intent){
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleSignInAccount ->
                Log.d(LOG_TAG, "Signed in as ${googleSignInAccount.email}")

                val credential = GoogleAccountCredential.usingOAuth2(
                    this,
                    Collections.singleton(DriveScopes.DRIVE_FILE)
                )
                credential.setSelectedAccount(googleSignInAccount.account)
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential)
                    .setApplicationName("QshaRe")
                    .build()

                driveServiceHelper = DriveServiceHelper(googleDriveService)
                signedIn = true
            }
            .addOnFailureListener {exception ->
                Log.e(LOG_TAG, "Unable to sign in, $exception")
            }
    }

    /**
     * Opens a file metadata and contents (useful for debug)
     */
    private fun openFileFromFilePicker(uri: Uri) {
        if(this::driveServiceHelper.isInitialized){
            Log.d(LOG_TAG, "Opening ${uri.path}")

            driveServiceHelper.openFileUsingStorageAccessFramework(contentResolver, uri, this)
                .addOnSuccessListener { nameAndContent ->
                    Log.d(LOG_TAG, "Got file: ${nameAndContent.first}")
                }
        }
    }

    /**
     * Uploads a file at a specified URI to google drive
     */
    private fun uploadFileFromFilePicker(uri: Uri){
        if(this::driveServiceHelper.isInitialized){
            driveServiceHelper.openFileUsingStorageAccessFramework(contentResolver, uri, this)
                .addOnSuccessListener { nameAndContent ->
                    Log.d(LOG_TAG, "Got file: ${nameAndContent.first}")
                    driveServiceHelper.uploadFileToDrive(this, nameAndContent, uri)
                        .addOnSuccessListener { driveFileRef ->
//                            driveServiceHelper.getDriveLink(driveFileRef.id)
//                                .addOnSuccessListener { link ->
//                                    Log.d(LOG_TAG, "Got url, $link")
//                                }
                            Log.d(LOG_TAG, "Name: ${nameAndContent.first?.first}, Description: ${nameAndContent.first?.second}")

                            // Send to ItemListFrag to process
                            val fragment = ItemListFragment()
                            val args = Bundle().apply {
                                putSerializable(KEY_NEW_FILE, driveFileRef.webViewLink as String)
                                putSerializable(KEY_FILE_NAME, nameAndContent.first?.first as String)
                                putSerializable(KEY_FILE_DESC, nameAndContent.first?.second as String)
                            }
                            fragment.apply {
                                arguments = args
                            }

                            driveServiceHelper.setSharePublic(driveFileRef.id)
                                .addOnSuccessListener {
                                    Log.d(LOG_TAG, "Successfully set share permission!")
                                }

                            // Start transaction
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .commit()
                        }
                }
        }
    }

    /**
     * Brings up QR code detail view of selected item
     */
    override fun onItemSelected(itemId: UUID) {
        val fragment = ItemDetailFragment.newInstance(itemId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
