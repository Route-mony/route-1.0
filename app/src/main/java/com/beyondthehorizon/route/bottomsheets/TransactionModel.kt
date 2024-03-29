package com.beyondthehorizon.route.bottomsheets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.FundAmountActivity
import com.beyondthehorizon.route.views.requestfunds.RequestFundActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransactionModel : BottomSheetDialogFragment() {
    private lateinit var mListener: TransactionBottomSheetListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.send_mobile_money_dialog_layout, container, false)
        pref = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()
        var mobileInput = v.findViewById<TextView>(R.id.mobileNumber)
        var myPhoneButton = v.findViewById<Button>(R.id.myPhone)
        var sendButton = v.findViewById<Button>(R.id.mobileButton)
        var contactsButton = v.findViewById<ImageView>(R.id.imgSearch)
        myPhoneButton.visibility = View.VISIBLE
        myPhoneButton.setOnClickListener {
            var phone = pref.getString(MyPhoneNumber, "")
            var intent = Intent(activity, FundAmountActivity::class.java)
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, MOBILE_TRANSACTION)
            editor.apply()
            intent.putExtra(PHONE_NUMBER, phone)
            startActivity(intent)
        }

        contactsButton.setOnClickListener {
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, MOBILE_TRANSACTION)
            editor.apply()
            var intent = Intent(activity, RequestFundActivity::class.java)
            startActivity(intent)
        }

        sendButton.setOnClickListener {
            val phone = mobileInput.text.trim().toString()
            var intent = Intent(activity, FundAmountActivity::class.java)
            if (phone.length < 10) {
                mobileInput.error = "Enter valid phone number"
            } else {
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, MOBILE_TRANSACTION)
                editor.apply()
                intent.putExtra(PHONE_NUMBER, phone)
                startActivity(intent)
            }
        }
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as TransactionBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface TransactionBottomSheetListener {
        fun transactionBottomSheetListener(amount: String?, ben_account: String?, ben_ref: String?)
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}