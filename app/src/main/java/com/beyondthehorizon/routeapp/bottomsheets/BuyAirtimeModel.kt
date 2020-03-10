package com.beyondthehorizon.routeapp.bottomsheets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.BUY_AIRTIME
import com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY
import com.beyondthehorizon.routeapp.views.ConfirmFundRequestActivity
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.beyondthehorizon.routeapp.views.RequestFundsActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BuyAirtimeModel : BottomSheetDialogFragment() {
    private var mListener: AirtimeBottomSheetListener? = null
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.send_mobile_money_dialog_layout, container, false)
        pref = activity?.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()
        var mobileInput = v.findViewById<TextView>(R.id.mobileNumber)
        var myPhoneButton = v.findViewById<Button>(R.id.myPhone)
        var sendButton = v.findViewById<Button>(R.id.mobileButton)
        var contactsButton = v.findViewById<ImageView>(R.id.imgSearch)
        myPhoneButton.visibility = View.VISIBLE
        myPhoneButton.setOnClickListener {
            val phone = pref!!.getString("Phone", "")
            var intent = Intent(activity, FundAmountActivity::class.java)
            intent.putExtra("Phone", phone)
            startActivity(intent)
        }

        contactsButton.setOnClickListener {
            var intent = Intent(activity, RequestFundsActivity::class.java)
            intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, BUY_AIRTIME)
            startActivity(intent)
        }

        sendButton.setOnClickListener {
            val phone = mobileInput.text.trim()
            if(phone.length < 10){
                mobileInput.error = "Enter valid phone number"
            }
            else {
                var intent = Intent(activity, FundAmountActivity::class.java)
                intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, BUY_AIRTIME)
                intent.putExtra("Phone", phone)
                startActivity(intent)
            }
        }
       return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as AirtimeBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface AirtimeBottomSheetListener {
        fun airtimeBottomSheetListener(amount: String?, ben_account: String?, ben_ref: String?)
    }

    companion object {
        const val TAG = "AirtimeMoneyBottomModel"
    }
}