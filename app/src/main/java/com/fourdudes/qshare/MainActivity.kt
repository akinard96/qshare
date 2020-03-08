package com.fourdudes.qshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
                    //TODO start scan code activity
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
                //TODO start about activity
                true
            }
            R.id.help_menu_item -> {
                //TODO start help activity
                true
            }
            R.id.settings_menu_item -> {
                //TODO start settings activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
