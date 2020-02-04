package com.beyondthehorizon.routeapp.views

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityFundAmountBinding
import com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES
import kotlinx.android.synthetic.main.activity_fund_amount.*
import java.lang.Exception


class FundAmountActivity : AppCompatActivity() {

    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding: ActivityFundAmountBinding = DataBindingUtil.setContentView(this, R.layout.activity_fund_amount)
        var editor: SharedPreferences.Editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        var prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        try {
            username = prefs.getString("Username", "").toString()
            binding.requestUserName.text = username

            binding.btnOne.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnOne.text}"
            }

            binding.btnTwo.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnTwo.text}"
            }

            binding.btnThree.setOnClickListener {
                binding.txtAmount.text= "${binding.txtAmount.text}${binding.btnThree.text}"
            }

            binding.btnFour.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnFour.text}"
            }

            binding.btnFive.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnFive.text}"
            }

            binding.btnSix.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnSix.text}"
            }

            binding.btnZero.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnZero.text}"
            }

            binding.btnZeroZero.setOnClickListener {
                binding.txtAmount.text = "${binding.txtAmount.text}${binding.btnZeroZero.text}"
            }

            binding.btnClear.setOnClickListener{
                if(binding.txtAmount.text.length <= 1){
                    binding.txtAmount.text = ""
                }
                else {
                    binding.txtAmount.text = binding.txtAmount.text .toString().removeRange(binding.txtAmount.text.lastIndex - 1, binding.txtAmount.text .lastIndex)
                }
            }

            binding.btnRequest.setOnClickListener{
                if(binding.txtAmount.text.isNullOrEmpty()){
                    Toast.makeText(this, "Please enter amount to request", Toast.LENGTH_LONG).show()
                }
                else if(binding.txtAmount.text.toString().toInt() <= 0){
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_LONG).show()
                }
                else{
                    editor.putString("Amount", binding.txtAmount.text.toString())
                    Toast.makeText(this, "Great job", Toast.LENGTH_LONG).show()
                }
            }
        }
        catch (ex:Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }
}
