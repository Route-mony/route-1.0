package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityFundAmountBinding
import com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES
import java.text.DecimalFormat
import java.text.NumberFormat


class FundAmountActivity : AppCompatActivity() {

    private var username = ""
    private var amount = ""
    private lateinit var format: NumberFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding: ActivityFundAmountBinding = DataBindingUtil.setContentView(this, R.layout.activity_fund_amount)
        var editor: SharedPreferences.Editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        var prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        var intent = Intent(this, ConfirmFundRequestActivity::class.java)
        format = DecimalFormat("#,###")
        try {
            username = prefs.getString("Username", "").toString()
            binding.requestUserName.text = username

            binding.btnOne.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnOne.text}")
            }

            binding.btnTwo.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnTwo.text}")
            }

            binding.btnThree.setOnClickListener {
                binding.txtAmount.text= formatAmount("${amount}${binding.btnThree.text}")
            }

            binding.btnFour.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnFour.text}")
            }

            binding.btnFive.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnFive.text}")
            }

            binding.btnSix.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnSix.text}")
            }

            binding.btnZero.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnZero.text}")
            }

            binding.btnZeroZero.setOnClickListener {
                binding.txtAmount.text = formatAmount("${amount}${binding.btnZeroZero.text}")
            }

            binding.btnClear.setOnClickListener{
                if(amount.length <= 1){
                    binding.txtAmount.text = ""
                }
                else {
                    binding.txtAmount.text = formatAmount(amount.removeRange(amount.lastIndex - 1, amount.lastIndex))
                }
            }

            binding.btnRequest.setOnClickListener{
                if(binding.txtAmount.text.isNullOrEmpty()){
                    Toast.makeText(this, "Please enter amount to request", Toast.LENGTH_LONG).show()
                }
                else if(amount.toInt() <= 0){
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_LONG).show()
                }
                else{
                    editor.putString("Amount", amount)
                    editor.apply()
                    startActivity(intent)
                }
            }

            binding.arrowBack.setOnClickListener{
                startActivity(Intent(this, RequestFundsActivity::class.java))
            }
        }
        catch (ex:Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun formatAmount(amt: String): String{
        amount = amt
        return format.format(amt.toInt())
    }
}
