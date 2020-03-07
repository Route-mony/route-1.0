package com.beyondthehorizon.routeapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityAddMoneyBinding

class AddMoneyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMoneyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_money)

        binding.btnCardOption.setOnClickListener{
            startActivity(Intent(this, CardActivity::class.java))
        }

        binding.btnMpesaOption.setOnClickListener{
            startActivity(Intent(this, MpesaActivity::class.java))
        }

        binding.arrowBack.setOnClickListener{
            onBackPressed()
        }
    }
}
