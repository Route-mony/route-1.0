package com.beyondthehorizon.route.bottomsheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.BulkyRequestModel
import com.beyondthehorizon.route.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.add_bulk_item_layout.view.*

class AddBulkRequestItemBottomSheet : BottomSheetDialogFragment() {
    private lateinit var mListener: AddBulkRequestItemBottomSheetListener
    private lateinit var pref: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.add_bulk_item_layout, container, false)
        pref = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        //login button click of custom layout
        v.dialogAddBtn.setOnClickListener {
            //dismiss dialog
            //get text from EditTexts of custom layout
            val name = v.itemName.text.toString().trim()
            val quantity = v.itemQuantity.text.toString().trim()
            val amount = v.itemAmount.text.toString().trim()
            //set the input text in TextView
            if (name.isEmpty()) {
                v.itemName.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (quantity.isEmpty()) {
                v.itemQuantity.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (amount.isEmpty()) {
                v.itemAmount.error = "Cannot be empty"
                return@setOnClickListener
            }
            val arrayList = ArrayList<BulkyRequestModel>()
            val arrayListJson = ArrayList<String>()
            val item = JsonObject()
            item.addProperty("title", name)
            item.addProperty("unit_price", amount)
            item.addProperty("quantity", quantity)
            arrayList.add(BulkyRequestModel(name, amount, quantity))
            arrayListJson.add(item.toString())
            mListener.addNewBulkRequestItem(arrayList, arrayListJson)
            dismiss()
        }
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as AddBulkRequestItemBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    interface AddBulkRequestItemBottomSheetListener {
        fun addNewBulkRequestItem(arrayList: ArrayList<BulkyRequestModel>, arrayListJson2: ArrayList<String>)
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}