package com.beyondthehorizon.routeapp.bottomsheets

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.beyondthehorizon.routeapp.views.RequestFundsActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_buy_airtime.*

class BuyAirtimeDialogFragment : BottomSheetDialogFragment() {
    private lateinit var editor:SharedPreferences.Editor
    private  lateinit var prefs:SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_buy_airtime, container, false)
        editor = activity!!.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = activity!!.getSharedPreferences(REG_APP_PREFERENCES, 0)
        var phone = view.findViewById<TextView>(R.id.mobile_number)
        var btnBuy = view.findViewById<Button>(R.id.buy_airtime_button)
        var btnContactSearch = view.findViewById<ImageView>(R.id.img_search)

        //Open phonebook
        btnContactSearch.setOnClickListener {
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, BUY_AIRTIME)
            editor.apply()
            startActivity(Intent(activity, RequestFundsActivity::class.java))
        }
        btnBuy.setOnClickListener{
            var number = phone.text.trim().toString()
            if(number.length > 9){
                var intent = Intent(activity, FundAmountActivity::class.java)
                intent.putExtra(PHONE_NUMBER, number)
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, BUY_AIRTIME)
                editor.apply()
                startActivity(intent)
            }
            else{
                mobile_number.error = "Enter valid phone number!"
            }
        }
        return  view
    }
}
