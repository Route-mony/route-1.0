package com.beyondthehorizon.route.bottomsheets

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_reject_reason.view.*

class EnterReasonBottomSheet(private val onInputListener: OnInputListener) :
    BottomSheetDialogFragment() {
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_reject_reason, container, false)

        //login button click of custom layout
        v.dialogButtonReason.setOnClickListener {

            val reason: String = v.enterReason.text.toString()
            if (reason.isEmpty()) {
                Toast.makeText(requireActivity(), "Enter Reason", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            onInputListener.onReasonListener(reason)
            dismiss()
        }
        return v;
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}