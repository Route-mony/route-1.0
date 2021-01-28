package com.beyondthehorizon.route.loan

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_loan.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class LoanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)

        btn_apply_loan.setOnClickListener {
            val pr = ProgressDialog(this@LoanActivity)
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({
                // yourMethod();
                pr.dismiss()
                val intent = Intent(this@LoanActivity, ApplyLoanActivity::class.java)
                startActivity(intent)
            }, 1000)
        }

        btn_home.setOnClickListener {
            val intent = Intent(this@LoanActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_receipt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoanActivity, ReceiptActivity::class.java)
            startActivity(intent)
        })

        btn_transactions.setOnClickListener {
            val intent = Intent(this@LoanActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@LoanActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        arrow_back.setOnClickListener {
            onBackPressed()
        }
    }
}