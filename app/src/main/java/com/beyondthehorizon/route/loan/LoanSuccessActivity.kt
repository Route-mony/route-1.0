package com.beyondthehorizon.route.loan

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_loan.*
import kotlinx.android.synthetic.main.activity_loan_success.*
import kotlinx.android.synthetic.main.activity_loan_success.arrow_back
import kotlinx.android.synthetic.main.nav_bar_layout.*

class LoanSuccessActivity : AppCompatActivity() {
    lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_success)
        pref = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        message.text = "Dear ${pref.getString("FullName", "")}, Thank you for using Route.Please continue using Route services to grow your loan limit and qualify for a loan."

        btn_apply_loan_done.setOnClickListener {
            val pr = ProgressDialog(this@LoanSuccessActivity)
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({
                // yourMethod();
                pr.dismiss()
                val intent = Intent(this@LoanSuccessActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                startActivity(intent)
            }, 1000)
        }

        btn_home.setOnClickListener {
            val intent = Intent(this@LoanSuccessActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_receipt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoanSuccessActivity, ReceiptActivity::class.java)
            startActivity(intent)
        })
        btn_transactions.setOnClickListener {
            val intent = Intent(this@LoanSuccessActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@LoanSuccessActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        arrow_back.setOnClickListener {
            onBackPressed()
        }
    }
}