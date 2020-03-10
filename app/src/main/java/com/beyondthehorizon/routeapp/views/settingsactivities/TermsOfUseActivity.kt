package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.MainActivity
import kotlinx.android.synthetic.main.activity_terms_of_use.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class TermsOfUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_use)

        back.setOnClickListener {
            onBackPressed()
        }

        btn_home.setOnClickListener {
            val intent = Intent(this@TermsOfUseActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@TermsOfUseActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
