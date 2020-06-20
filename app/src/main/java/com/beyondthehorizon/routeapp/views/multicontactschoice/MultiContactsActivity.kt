package com.beyondthehorizon.routeapp.views.multicontactschoice

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.FavoriteMultiChoiceContactsFragment
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.MultiChoiceContactsFragment
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.SendToManyActivity
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.receipt.ui.main.RequestFundSectionsPagerAdapter
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.split.bill.SplitBillsDetailsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_multi_contacts.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class MultiContactsActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var activity: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_contacts)
        val sectionsPagerAdapter = RequestFundSectionsPagerAdapter(supportFragmentManager)
        prefs = this.getSharedPreferences(REG_APP_PREFERENCES, 0)

        tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(MultiChoiceContactsFragment(), "Phone Number")
        sectionsPagerAdapter.addFragment(FavoriteMultiChoiceContactsFragment(), "Favorites")
        // sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Outs")

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        btn_home.setOnClickListener {
            val intent = Intent(this@MultiContactsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@MultiContactsActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@MultiContactsActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@MultiContactsActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        chooseContacts.setOnClickListener {
            activity = prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "").toString()
            prefs.edit().putString(GROUP_IS_SAVED, "NO").apply()
            if (activity.compareTo(SEND_MONEY) == 0) {
                val intent = Intent(this@MultiContactsActivity, SendToManyActivity::class.java)
                startActivity(intent)
            } else if (activity.compareTo(SPLIT_BILL) == 0) {
                val intent = Intent(this@MultiContactsActivity, SplitBillsDetailsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}