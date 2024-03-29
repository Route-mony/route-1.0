package com.beyondthehorizon.route.views.settingsactivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.utils.DatePickerHelper
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_change_password.back
import kotlinx.android.synthetic.main.activity_download_statement.*
import kotlinx.android.synthetic.main.nav_bar_layout.*
import java.util.*

class DownloadStatementActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var datePicker: DatePickerHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_statement)

        startDate.setOnClickListener(this)
        endDate.setOnClickListener(this)
        datePicker = DatePickerHelper(this)

        btn_home.setOnClickListener {
            val intent = Intent(this@DownloadStatementActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@DownloadStatementActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@DownloadStatementActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@DownloadStatementActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showDatePickerDialog(view: View?) {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        val btnDate = findViewById<Button>(view!!.id)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                val selectedDate = "${dayStr}/${monthStr}/${year}"
                btnDate.setText(selectedDate)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startDate -> {
                showDatePickerDialog(v)
            }
            R.id.endDate -> showDatePickerDialog(v)
        }
    }
}

