package com.fourdudes.qshare

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.leinardi.android.speeddial.SpeedDialView
import java.net.URI
import java.util.*
import java.util.stream.Stream

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

    override fun onStart() {
        super.onStart()
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if(account == null){ //user is not signed in
            requestSignIn()
        }
        else{ //user is signed in
            //instantiate drive service helper
            driveServiceHelper = DriveServiceHelper(getGoogleDriveService(account))

            //check intent for data and upload if given
            checkIntent()
        }
    }

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

        //handle an intent(from intent filter)
        // Figure out what to do based on the intent type
//        if (intent?.type == "*/*") {
//            intent.data?.let { uploadFileFromFilePicker(it) }
//        } else {
//            Log.d(LOG_TAG, "unsupported intent type, ${intent.type}")
//        }

        //TODO move this so that it only prompts when actually uploading a file
        //prompt the user to sign in via google drive


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

                    val chooserIntent = driveServiceHelper.createFilePickerIntent()
                    startActivityForResult(chooserIntent, REQUEST_CODE_FILE)

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
//                openFileFromFilePicker(fileUri)
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
        val client = GoogleSignIn.getClient(applicationContext, signInOptions)

        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.apply {
            setTitle("Let us access your Google Account")
            setMessage("We need to be able to probe the depths of your Google Drive so that we can do our thing you know...")
            setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
            })
            setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                Toast.makeText(applicationContext, "We're serious we really need that, you are banished from QshaRe", Toast.LENGTH_LONG).show()
                finish()
            })
            create()
            show()
        }


    }

    private fun handleSignInResult(result: Intent){
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleSignInAccount ->
                Log.d(LOG_TAG, "Signed in as ${googleSignInAccount.email}")
                driveServiceHelper = DriveServiceHelper(getGoogleDriveService(googleSignInAccount))
                checkIntent()
            }
            .addOnFailureListener {exception ->
                Log.e(LOG_TAG, "Unable to sign in, $exception")
            }
    }

    private fun getGoogleDriveService(googleAcoount: GoogleSignInAccount) : Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            this,
            Collections.singleton(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = googleAcoount.account
        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential)
            .setApplicationName("QshaRe")
            .build()
        return googleDriveService
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

    private fun checkIntent(){
        intent.clipData.let { data ->
            data?.getItemAt(0)?.uri?.let { uri ->
                uploadFileFromFilePicker(uri)
                Log.d(LOG_TAG, "Intent with data received, ${intent.clipData?.getItemAt(0)?.uri}")
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
