package com.beyondthehorizon.routeapp.bottomsheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.enter_pin_transaction_pin.view.*

class EnterPinBottomSheet : BottomSheetDialogFragment(){
    private lateinit var mListener: EnterPinBottomSheetBottomSheetListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.enter_pin_transaction_pin, container, false)
        pref = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()

        //login button click of custom layout
        v.dialogButtonPin.setOnClickListener {

            val pin: String =  v.enterPin.text.toString()
            if (pin.isEmpty()) {
                Toast.makeText(requireActivity(), "Enter pin", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mListener.enterPinDialog(pin)
            dismiss()
        }
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as EnterPinBottomSheetBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface EnterPinBottomSheetBottomSheetListener {
        fun enterPinDialog(pin: String)
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}