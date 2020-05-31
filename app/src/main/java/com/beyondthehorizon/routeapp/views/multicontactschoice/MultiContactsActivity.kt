package com.beyondthehorizon.routeapp.views.multicontactschoice

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.FavoriteMultiChoiceContactsFragment
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.MultiChoiceContactsFragment
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.SendToManyActivity
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.receipt.ui.main.RequestFundSectionsPagerAdapter
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_multi_contacts.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class MultiContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_contacts)
        val sectionsPagerAdapter = RequestFundSectionsPagerAdapter(supportFragmentManager)

        tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(MultiChoiceContactsFragment(), "Phone Number")
        sectionsPagerAdapter.addFragment(FavoriteMultiChoiceContactsFragment(), "Favorites")
//        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Outs")

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
            val intent = Intent(this@MultiContactsActivity, SendToManyActivity::class.java)
            startActivity(intent)
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}