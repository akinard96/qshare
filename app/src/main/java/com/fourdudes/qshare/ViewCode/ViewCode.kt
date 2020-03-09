package com.fourdudes.qshare.ViewCode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.fourdudes.qshare.R

class ViewCode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_code)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_code_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
