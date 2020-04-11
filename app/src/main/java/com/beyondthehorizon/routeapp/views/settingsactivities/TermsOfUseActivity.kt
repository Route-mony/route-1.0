package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
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

        btn_pool.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@TermsOfUseActivity, ReceiptActivity::class.java)
            startActivity(intent)
        })

        btn_transactions.setOnClickListener {
            val intent = Intent(this@TermsOfUseActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
