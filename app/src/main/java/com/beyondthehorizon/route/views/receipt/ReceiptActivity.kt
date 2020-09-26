package com.beyondthehorizon.route.views.receipt

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.ReceiptAdapter
import com.beyondthehorizon.route.bottomsheets.ReceiptDetailsBottomModel
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ui.main.ReceivedReceiptFragment
import com.beyondthehorizon.route.views.receipt.ui.main.SectionsPagerAdapter
import com.beyondthehorizon.route.views.receipt.ui.main.SentReceiptFragment
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_receipt.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class ReceiptActivity : AppCompatActivity(), ReceiptAdapter.ReceiptInterface {
    override fun onReceiptClicked(receipt: String, receipt_type: String) {
        val receiptDetailsBottomModel = ReceiptDetailsBottomModel()
        receiptDetailsBottomModel.show(supportFragmentManager, "Receipt")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        btn_receipt.setImageResource(R.drawable.ic_nav_receipts);
        txt_pool.setTextColor(resources.getColor(R.color.colorButton))

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(SentReceiptFragment(), "Sent Receipts")
        sectionsPagerAdapter.addFragment(ReceivedReceiptFragment(), "Received Receipts")
//        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Outs")

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)



        btn_home.setOnClickListener {
            val intent = Intent(this@ReceiptActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@ReceiptActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_transactions.setOnClickListener {
            val intent = Intent(this@ReceiptActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}