package com.beyondthehorizon.route.bottomsheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.AddBulkItemLayoutBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.AddBulkRequestItemBottomSheetListener
import com.beyondthehorizon.route.models.BulkyRequestModel
import com.beyondthehorizon.route.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.add_bulk_item_layout.view.*
import org.w3c.dom.Text

class AddBulkRequestItemBottomSheet(private val addBulkRequestItemBottomSheetListener: AddBulkRequestItemBottomSheetListener) : BottomSheetDialogFragment() {
    private var _binding: AddBulkItemLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AddBulkItemLayoutBinding.inflate(layoutInflater, container, false)
        val view = binding.root
            binding.dialogAddBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.itemName.text)) {
                binding.itemName.error = "Enter item name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.itemQuantity.text)) {
                binding.itemQuantity.error = "Enter item quantity"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.itemAmount.text)) {
                binding.itemAmount.error = "Enter item amount"
                return@setOnClickListener
            }
                addBulkRequestItemBottomSheetListener.onAddBulkRequestListener()
                addBulkRequestItemBottomSheet.addBulkRequestItemBottomSheet(BulkyRequestModel(

                ));
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
        return view
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
}
