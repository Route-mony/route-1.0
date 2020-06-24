package com.beyondthehorizon.routeapp.views.split.bill

import android.app.ProgressDialog
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
    private var amountTotal: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bills_details)
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        var amount = prefs.getString(BILL_AMOUNT, "")?.let { Integer.parseInt(it) }
        format = DecimalFormat("#,###")
        tvBillAmount.text = "Kes ${format.format(amount)}"
        val token = "Bearer " + prefs.getString(USER_TOKEN, "")
        val gsonn = Gson()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        btnSplit.setOnClickListener {
//            val recipients = gsonn.toJson(arrayList.map { it.id to it.amount })
            val recipients: String = gsonn.toJson(arrayList)
            val amount: Double = amountTotal
            val group = tvGroup.text.toString()
            if (group.isNullOrEmpty()) {
                tvGroup.error = "Please provide your group name";
            } else {
                progressDialog.show()
                splitBill(this, recipients, group, token).setCallback { e, result ->
                    if (result.has("success")) {
                        Toast.makeText(this, "Bill created successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Bill not created successfully", Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()
                }
            }

            //        val message = "You have successfully send ${tvBillTotal.text.toString()} to them.(sample reply)"
//        val intent = Intent(this, FundRequestedActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.putExtra("Message", message)
//        startActivity(intent)
//        finish()
        }


        usersAdapter = GroupSendToManyAdapter(this, this)
        { position ->
            run {}
        }
        bulkRequestRecycler.apply {
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
