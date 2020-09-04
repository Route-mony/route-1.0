package com.beyondthehorizon.routeapp.views.notifications

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.notifications.ui.main.ReceivedNotificationFragment
import com.beyondthehorizon.routeapp.views.notifications.ui.main.SentNotificationFragment
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.receipt.ui.main.RequestFundSectionsPagerAdapter
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_request_fund.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val sectionsPagerAdapter = RequestFundSectionsPagerAdapter(supportFragmentManager)

        tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(SentNotificationFragment(), "Sent")
        sectionsPagerAdapter.addFragment(ReceivedNotificationFragment(), "Received")
//        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Outs")

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        btn_home.setOnClickListener {
            val intent = Intent(this@NotificationsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@NotificationsActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@NotificationsActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@NotificationsActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun prevPage(view: View) {
        startActivity(Intent(this@NotificationsActivity, MainActivity::class.java))
        finish()
    }
}
