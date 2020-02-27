package com.beyondthehorizon.routeapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityRequestConfirmedBinding
import com.beyondthehorizon.routeapp.utils.Constants
import java.lang.Exception

class RequestConfirmedActivity : AppCompatActivity() {
   private lateinit var binding: ActivityRequestConfirmedBinding
    private lateinit var oldIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_confirmed)
        oldIntent = getIntent()
        try {
            binding.txtRequestInform.text = intent.getStringExtra("Message")
            binding.btnDone.setOnClickListener{
                startActivity(Intent(this, NotificationActivity::class.java))
            }
            binding.arrowBack.setOnClickListener{
                onBackPressed()
            }
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
