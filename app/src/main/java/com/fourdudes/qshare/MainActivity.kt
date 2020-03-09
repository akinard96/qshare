package com.fourdudes.qshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.fourdudes.qshare.AboutPage.AboutActivity
import com.fourdudes.qshare.HelpPage.HelpActivity
import com.fourdudes.qshare.Scan.ScanActivity
import com.fourdudes.qshare.Settings.SettingsActivity
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val speedDialView = findViewById<SpeedDialView>(R.id.speed_dial)
        speedDialView.inflate(R.menu.fab_actions_menu)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when(actionItem.id) {
                R.id.upload_fab -> {
                    //TODO present file picker
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
