package com.beyondthehorizon.routeapp.bottomsheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.models.MultiContactModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.edit_sendtomany_item_layout.view.*

class EditSendToManyBottomSheet : BottomSheetDialogFragment() {
    private lateinit var mListener: EditSendToManyBottomSheetListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.edit_sendtomany_item_layout, container, false)
        pref = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()

        val gson = Gson()
        val personData = arguments?.getString("personData")
        val itemPosition = arguments?.getString("itemPosition")
        val editPersonData = gson.fromJson(personData, MultiContactModel::class.java)

        v.username.text = editPersonData.username
        v.contact.text = editPersonData.phone_number
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        if (editPersonData.is_route) {
            Glide.with(context!!)
                    .load(editPersonData.image)
                    .centerCrop()
                    .error(R.drawable.group416)
                    .placeholder(R.drawable.group416)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into(v.profile_image)
        } else {
            Glide.with(context!!)
                    .load(editPersonData.image)
                    .centerCrop()
                    .error(R.drawable.default_avatar)
                    .placeholder(R.drawable.default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into(v.profile_image)
        }
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

            mListener.editSendToManyItem(MultiContactModel(
                    editPersonData.id,
                    editPersonData.username,
                    editPersonData.phone_number,
                    editPersonData.image,
                    addAmount,
                    editPersonData.is_route,
                    editPersonData.is_selected
            ), itemPosition!!.toInt())
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
        fun editSendToManyItem(updatedItem: MultiContactModel, position: Int)
    }

    companion object {
        const val TAG = "TransactionMoneyBottomModel"
    }
}