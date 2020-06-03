package com.beyondthehorizon.routeapp.views.split.bill

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.split.bill.fragments.SplitBillDetailsFragment

class NewSplitBillActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_split_bill)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SplitBillDetailsFragment()).commit()
    }
}
