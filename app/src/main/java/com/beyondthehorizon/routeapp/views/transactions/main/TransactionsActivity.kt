package com.beyondthehorizon.routeapp.views.transactions.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_services.*
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class TransactionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        tabs.setupWithViewPager(view_pager)
//        sectionsPagerAdapter.addFragment(SentFragment(), "Sent")
        sectionsPagerAdapter.addFragment(ReceivedFragment(), "Received")
        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Outs")
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        btn_home.setOnClickListener {
            val intent = Intent(this@TransactionsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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