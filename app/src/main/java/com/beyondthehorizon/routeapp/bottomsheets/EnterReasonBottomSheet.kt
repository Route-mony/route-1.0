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
import kotlinx.android.synthetic.main.dialog_reject_reason.view.*

class EnterReasonBottomSheet: BottomSheetDialogFragment(){
    private lateinit var mListener: EnterReasonBottomSheetListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_reject_reason, container, false)
        pref = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()

        //login button click of custom layout
        v.dialogButtonReason.setOnClickListener {

            val reason: String =  v.enterReason.text.toString()
            if (reason.isEmpty()) {
                Toast.makeText(activity!!, "Enter Reason", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mListener.enterReasonDialog(reason)
            dismiss()
        }
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as EnterReasonBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface EnterReasonBottomSheetListener {
        fun enterReasonDialog(reason: String)
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}