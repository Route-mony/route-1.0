package com.beyondthehorizon.routeapp.bottomsheets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.models.BulkyRequestModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.beyondthehorizon.routeapp.views.RequestFundsActivity
import com.beyondthehorizon.routeapp.views.requestfunds.RequestFundActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.add_bulk_item_layout.view.*
import kotlinx.android.synthetic.main.add_bulk_item_layout.view.dialogAddBtn
import kotlinx.android.synthetic.main.edit_sendtomany_item_layout.view.*

class EditSendToManyBottomSheet : BottomSheetDialogFragment() {
    private lateinit var mListener: EditSendToManyBottomSheetListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.edit_sendtomany_item_layout, container, false)
        pref = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()

        //login button click of custom layout
        v.dialogAddBtn.setOnClickListener {
            //dismiss dialog
            //get text from EditTexts of custom layout
            val username = v.username.text.toString().trim()
            val contact = v.contact.text.toString().trim()
            val addAmount = v.addAmount.text.toString().trim()
            //set the input text in TextView

            if (addAmount.isEmpty()) {
                v.addAmount.error = "Cannot be empty"
                return@setOnClickListener
            }
//            val arrayList = ArrayList<BulkyRequestModel>()
//            val arrayListJson = ArrayList<String>()
//            val item = JsonObject()
//            item.addProperty("title", name)
//            item.addProperty("unit_price", amount)
//            item.addProperty("quantity", quantity)
//            arrayList.add(BulkyRequestModel(name, amount, quantity))
//            arrayListJson.add(item.toString())

//            mListener.editSendToManyItem(arrayList, arrayListJson)
            dismiss()
//            if (arrayList.size > 1) {
////                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
//                for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
//                    amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
//                    Log.e("NAHAPA", amountTotal.toString())
//                }
//                totals.text = amountTotal.toString()
//                totalCard.visibility = View.VISIBLE
//                emptyList.visibility = View.GONE
//            }
//                else if (arrayList.size == 2) {
//                    totals.text = arrayList.get(1).amount
//                    totalCard.visibility = View.VISIBLE
//
//                }

        }
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as EditSendToManyBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface EditSendToManyBottomSheetListener {
        fun editSendToManyItem(arrayList: ArrayList<BulkyRequestModel>, arrayListJson2: ArrayList<String>)
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}