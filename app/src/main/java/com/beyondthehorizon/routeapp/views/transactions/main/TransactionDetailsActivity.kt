package com.beyondthehorizon.routeapp.views.transactions.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.settingsactivities.InviteFriendActivity
import kotlinx.android.synthetic.main.activity_transaction_details.*

class TransactionDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        shareReceipt.setOnClickListener {
            val intent = Intent(this@TransactionDetailsActivity, InviteFriendActivity::class.java)
            intent.putExtra("TYPE", "SHARE")
            startActivity(intent)
        }

    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}
