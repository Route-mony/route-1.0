package com.beyondthehorizon.route.views.requestfunds

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.requestfunds.ui.main.FavoriteContactsFragment
import com.beyondthehorizon.route.views.requestfunds.ui.main.RequestFundsFragment
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.receipt.ui.main.RequestFundSectionsPagerAdapter
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_request_fund.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class RequestFundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_fund)
        val sectionsPagerAdapter =  RequestFundSectionsPagerAdapter(supportFragmentManager)

        tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(RequestFundsFragment(), "Phone Number")
        sectionsPagerAdapter.addFragment(FavoriteContactsFragment(), "Favorites")
//        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Outs")

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        btn_home.setOnClickListener {
            val intent = Intent(this@RequestFundActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@RequestFundActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@RequestFundActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@RequestFundActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}