package com.beyondthehorizon.routeapp.paybillpayments

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import kotlinx.android.synthetic.main.activity_buy_goods.*
import kotlinx.android.synthetic.main.enter_pin_transaction_pin.view.*

class BuyGoodsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_goods)
        buyGoodsButton.setOnClickListener {

            if (tillNumber.text.toString().trim().isEmpty()) {
                tillNumber.error = "Can not be empty"
                return@setOnClickListener
            }
            if (tillAmount.text.toString().trim().isEmpty()) {
                tillAmount.error = "Can not be empty"
                return@setOnClickListener
            }
            makePayments()
        }
    }

    private fun makePayments() {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(this).inflate(R.layout.enter_pin_transaction_pin, viewGroup, false)
        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        val prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")

        val progressBar = CustomProgressBar()
        progressBar.show(this, "Please Wait...")

        val amount = tillAmount.text.toString().trim()
        val account = tillAmount.text.toString().trim()

        val pin = dialogView.enterPin.text.toString().trim()

        dialogView.dialogButtonPin.setOnClickListener {
            if (pin.isEmpty()) {
                dialogView.enterPin.error = "Enter Pin"
                return@setOnClickListener
            }

            Constants.sendMoney(this@BuyGoodsActivity, account, amount, pin, token, "MPESAWALLET")
                    .setCallback { e, result ->
                        Log.e("BuyGoodsActivity", result.toString())
                        progressBar.dialog.dismiss()
                        if (result.has("errors")) {
                            Toast.makeText(this@BuyGoodsActivity, result.get("errors").asJsonArray[0].toString(), Toast.LENGTH_LONG).show()
                        } else {
                            val message = result.get("data").asJsonObject.get("message").asString
                            val intent = Intent(this, FundRequestedActivity::class.java)
                            intent.putExtra("Message", message)
                            startActivity(intent)
                            finish()
                        }
                    }
        }

    }

    fun prevPage(view: View) {}
}
