package com.beyondthehorizon.routeapp.bottomsheets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.group_name_item.view.*

class EnterGroupBottomSheet(val msg:String): BottomSheetDialogFragment() {

    private lateinit var mListener: EnterGroupBottomSheet.EnterGroupNameBottomSheetListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.group_name_item, container, false)
        pref = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()

        v.dialogButtonOKk.setOnClickListener{

            val group: String =  v.group_input.text.toString()
            if (group.isEmpty()) {
                Toast.makeText(requireActivity(), "Enter group name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mListener.enterGroupNameDialog(group)
            dismiss()
        }

        v.dialogButtonNoo.setOnClickListener{
            val intent = Intent(context, FundRequestedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Message", msg)
            startActivity(intent)
            dismiss()
        }
        return v;
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as EnterGroupNameBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface EnterGroupNameBottomSheetListener{
        fun enterGroupNameDialog(group: String)
    }
}