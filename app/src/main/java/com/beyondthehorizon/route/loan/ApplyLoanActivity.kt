package com.beyondthehorizon.route.loan

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.LoanBottomSheetModel
import com.beyondthehorizon.route.bottomsheets.MpesaMoneyBottomModel
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_apply_loan.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class ApplyLoanActivity : AppCompatActivity(), LoanBottomSheetModel.LoanBottomSheetListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_loan)

        btn_home.setOnClickListener {
            val intent = Intent(this@ApplyLoanActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_receipt.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ApplyLoanActivity, ReceiptActivity::class.java)
            startActivity(intent)
        })
        btn_transactions.setOnClickListener {
            val intent = Intent(this@ApplyLoanActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@ApplyLoanActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
        arrow_back.setOnClickListener {
            onBackPressed()
        }

        loanAmount.setText("Kes 2,000.00")
        receiveAmount.setText("Kes 1,440.00")
        tenor.setText("21 Days")
        serviceFee.setText("Kes 560.00")
        interst.setText("0.00")
        dueDate.setText("")
        nxt_apply_loan.setOnClickListener {
            val pr = ProgressDialog(this)
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({
                // yourMethod();
                pr.dismiss()
                val loanBottomModel = LoanBottomSheetModel()
                loanBottomModel.show(supportFragmentManager, "Submit Loan");
            }, 1000)
        }
    }
    override fun lnMethodBottomSheetListener(amount: String?, ben_account: String?, ben_ref: String?) {
        // TODO("Not yet implemented")
    }
}