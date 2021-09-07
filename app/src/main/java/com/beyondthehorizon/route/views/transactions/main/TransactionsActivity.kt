package com.beyondthehorizon.route.views.transactions.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class TransactionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        btn_transactions.setImageResource(R.drawable.ic_nav_transactions)
        txt_transactions.setTextColor(resources.getColor(R.color.colorButton))
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        tabs.setupWithViewPager(view_pager)
//        sectionsPagerAdapter.addFragment(SentFragment(), "Sent")
        sectionsPagerAdapter.addFragment(ReceivedFragment(), "Cash In")
        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Out")

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        btn_home.setOnClickListener {
            val intent = Intent(this@TransactionsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@TransactionsActivity, ReceiptActivity::class.java)
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@TransactionsActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}