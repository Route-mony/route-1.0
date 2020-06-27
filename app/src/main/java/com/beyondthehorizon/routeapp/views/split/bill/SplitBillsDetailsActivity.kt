package com.beyondthehorizon.routeapp.views.split.bill

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.routeapp.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.routeapp.models.MultiContactModel
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_split_bills_details.*
import java.lang.Boolean.FALSE
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
    private lateinit var token: String
    private var amountTotal: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bills_details)
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        var amount = prefs.getString(BILL_AMOUNT, "").toString().toDouble()
        format = DecimalFormat("#,###")
        tvBillAmount.text = "Kes ${format.format(amount)}"
        val isExistingGroup = prefs.getBoolean(EXISTING_GROUP, FALSE)
        token = "Bearer " + prefs.getString(USER_TOKEN, "")
        val gson = Gson()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        back.setOnClickListener {
            super.onBackPressed()
        }
        btnSplit.setOnClickListener {
            val recipients: String = gson.toJson(arrayList)
            val amount: Double = amountTotal
            val group = tvGroup.text.toString()
            if (group.isNullOrEmpty()) {
                tvGroup.error = "Please provide your group name";
            } else {
                progressDialog.show()
                splitBill(this, recipients, group, token).setCallback { e, result ->
                    if (result.has("data")) {
                        val message = "You have successfully created a Ksh. ${NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)} bill."
                        val intent = Intent(this, FundRequestedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("Message", message)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("TAG", e.message.toString())
                        Toast.makeText(this, "Request failed", Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()
                }
            }
        }

        usersAdapter = GroupSendToManyAdapter(this, this)
        {
            run {}
        }
        bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }

//        arrayList.add(MultiContactModel("id", "Name"
//                , "Quantity", "Quantity", "Amount", is_route = false, is_selected = false))
//        usersAdapter.setContact(arrayList)
        if (isExistingGroup) {
            fetchGroup()
        } else {
            sendToManyFunction()
        }

    }

    private fun sendToManyFunction() {
        val gson = Gson()
        try {
            val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
            arrayList = gson.fromJson(prefs.getString(MY_MULTI_CHOICE_SELECTED_CONTACTS, ""), type)

            Log.e("NOWNOW", prefs.getString(MY_MULTI_CHOICE_SELECTED_CONTACTS, ""))
            usersAdapter.setContact(arrayList)
        } catch (ex: java.lang.Exception) {
            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }

    private fun fetchGroup() {
        val groupId = prefs.getString(GROUP_ID, "")
        getSplitBill(this, groupId, token).setCallback { e, result ->
            try {
                if (result.has("data")) {
                    val data = result.get("data").asJsonObject.get("request").asJsonObject
                    val bill = data.get("bills").asJsonArray
                    tvGroup.setText(data.get("reason").asString)
//                    tvGroup.isEnabled = false
                    amountTotal = data.get("total_requested").asDouble
                    tvBillTotal.text = "Kes ${NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)}"
                    arrayList.clear()
                    for (item in bill) {
                        val recipient = item.asJsonObject.get("recipient").asJsonObject
                        arrayList.add(MultiContactModel(
                                recipient.get("id").asString,
                                recipient.get("first_name").asString + " " + recipient.get("last_name").asString,
                                recipient.get("phone_number").asString,
                                recipient.get("image").asString,
                                item.asJsonObject.get("amount").asInt.toString(),
                                true,
                                true
                        ))
                    }
                    usersAdapter.setContact(arrayList)
                } else {
                    Toast.makeText(this, "Unable to fetch split group", Toast.LENGTH_LONG)
                }
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }
    }

    override fun editSendToManyItem(updatedItem: MultiContactModel, position: Int) {
        arrayList.set(position, updatedItem)
        usersAdapter.setContact(arrayList)
        amountTotal = 0.0
        for (multiContactModel: MultiContactModel in arrayList) {
            amountTotal += multiContactModel.amount.toInt()
        }
        tvBillTotal.text = "Kes ${NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)}"
    }

    override fun updateItem(item: String, itemPosition: String) {
        val editSendToManyBottomSheet = EditSendToManyBottomSheet();
        val bundle = Bundle()
        bundle.putString("personData", item)
        bundle.putString("itemPosition", itemPosition)
        bundle.putString("title", "Edit Split")
        editSendToManyBottomSheet.arguments = bundle
        editSendToManyBottomSheet.show(supportFragmentManager, "Edit Contact");
    }
}
