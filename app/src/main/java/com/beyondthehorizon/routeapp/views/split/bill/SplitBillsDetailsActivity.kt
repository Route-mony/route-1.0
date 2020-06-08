package com.beyondthehorizon.routeapp.views.split.bill

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.routeapp.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.routeapp.models.MultiContactModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.MY_MULTI_CHOICE_SELECTED_CONTACTS
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_split_bills_details.*
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SplitBillsDetailsActivity : AppCompatActivity(), EditSendToManyBottomSheet.EditSendToManyBottomSheetListener, GroupSendToManyAdapter.SendToManyInterface {

    var arrayList = ArrayList<MultiContactModel>()
    val arrayListJson = ArrayList<String>()
    lateinit var usersAdapter: GroupSendToManyAdapter
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences
    private lateinit var format: NumberFormat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bills_details)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        var amount = prefs.getString(Constants.BILL_AMOUNT, "")?.let { Integer.parseInt(it) }
        format = DecimalFormat("#,###")
        split_amount.text = "Split: ${format.format(amount)}"

        btnSend.setOnClickListener {
            val message = "You have successfully send ${total_amount.text.toString()} to them.(sample reply)"
            val intent = Intent(this, FundRequestedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Message", message)
            startActivity(intent)
            finish()
        }


        usersAdapter = GroupSendToManyAdapter(this, this) { position ->
            run {
                var amountTotal = 0
                if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
                    for (bulkyRequestModel: MultiContactModel in arrayList.subList(1, arrayList.size)) {
//                        amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
                    }
//                    totals.text = amountTotal.toString()
//                    totalCard.visibility = View.VISIBLE
//                    emptyList.visibility = View.GONE
                } else {
//                    totalCard.visibility = View.GONE
//                    emptyList.visibility = View.VISIBLE
                }

            }
        }
//
        bill_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
        arrayList.add(MultiContactModel("id", "Name"
                , "Quantity", "Quantity", "Amount", is_route = false, is_selected = false))
        usersAdapter.setContact(arrayList)

        sendToManyFunction()
    }

    private fun sendToManyFunction() {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
        arrayList = gson.fromJson(prefs.getString(MY_MULTI_CHOICE_SELECTED_CONTACTS, ""), type)

        Log.e("NOWNOW", prefs.getString(MY_MULTI_CHOICE_SELECTED_CONTACTS, ""))
        usersAdapter.setContact(arrayList)
    }

    fun prevPage(view: View) {
        onBackPressed()
    }

    override fun editSendToManyItem(updatedItem: MultiContactModel, position: Int) {
        arrayList.set(position, updatedItem)
        usersAdapter.setContact(arrayList)
        var amountTotal = 0
        for (multiContactModel: MultiContactModel in arrayList) {
            amountTotal += multiContactModel.amount.toInt()
        }
        total_amount.text = "Total: ${NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)}"
    }

    override fun updateItem(item: String, itemPosition: String) {
        val editSendToManyBottomSheet = EditSendToManyBottomSheet();
        val bundle = Bundle()
        bundle.putString("personData", item)
        bundle.putString("itemPosition", itemPosition)
        editSendToManyBottomSheet.arguments = bundle
        editSendToManyBottomSheet.show(supportFragmentManager, "Edit Contact");
    }
}
