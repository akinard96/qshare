package com.fourdudes.qshare

import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.fourdudes.qshare.AboutPage.AboutActivity
import com.fourdudes.qshare.HelpPage.HelpActivity
import com.fourdudes.qshare.Scan.ScanActivity
import com.fourdudes.qshare.Settings.SettingsActivity
import com.fourdudes.qshare.list.ItemListFragment
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveFile
import com.leinardi.android.speeddial.SpeedDialView

const val REQUEST_CODE_FILE = 0
const val LOG_TAG = "4dudes.MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.list_holder)
        if(currentFragment == null) {
            val fragment = ItemListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.list_holder, fragment)
                .commit()
        }

        val speedDialView = findViewById<SpeedDialView>(R.id.speed_dial)
        speedDialView.inflate(R.menu.fab_actions_menu)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when(actionItem.id) {
                R.id.upload_fab -> {
                    Toast.makeText(this,
                        "open android file selector",
                        Toast.LENGTH_SHORT)
                    .show()
                    val intent = Intent(ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    val chooserIntent = createChooser(intent, "Choose a file!")
                    startActivityForResult(chooserIntent, REQUEST_CODE_FILE)
//                    intent.putExtra(CATEGORY_OPENABLE)
                    speedDialView.close()
                    true
                }
                R.id.scan_fab -> {
                    val intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                    speedDialView.close()
                    true
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
            }
        }
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
}
