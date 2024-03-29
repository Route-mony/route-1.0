package com.beyondthehorizon.route.views.multicontactschoice.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.route.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.route.bottomsheets.EnterGroupBottomSheet
import com.beyondthehorizon.route.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.route.models.MultiContactModel
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.FundRequestedActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_send_to_many.*
import timber.log.Timber
import java.lang.reflect.Type
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SendToManyActivity : AppCompatActivity(), EditSendToManyBottomSheet.EditSendToManyBottomSheetListener, GroupSendToManyAdapter.SendToManyInterface,
        EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener, EnterGroupBottomSheet.EnterGroupNameBottomSheetListener {

    var arrayList = ArrayList<MultiContactModel>()
    lateinit var usersAdapter: GroupSendToManyAdapter
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var token: String
    private lateinit var jsonn: String
    private lateinit var messagetxt: String
    private lateinit var gsonn: Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_to_many)
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        progressDialog = ProgressDialog(this)
        gsonn = Gson()

        submitButton.setOnClickListener {
            val enterPinBottomSheet = EnterPinBottomSheet()
            enterPinBottomSheet.show(supportFragmentManager, "Enter Pin")
        }


        usersAdapter = GroupSendToManyAdapter(this, this) { position ->
            run {}
        }

        bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
        arrayList.add(MultiContactModel("id", "Name", "Quantity", "Quantity", "Amount", is_route = false, is_selected = false))
        usersAdapter.setContact(arrayList)

        back.setOnClickListener {
            onBackPressed()
        }
        sendToManyFunction()
    }

    private fun sendToManyFunction() {
        val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
        arrayList = gsonn.fromJson(prefs.getString(MY_MULTI_CHOICE_SELECTED_CONTACTS, ""), type)
        jsonn = gsonn.toJson(arrayList)
        usersAdapter.setContact(arrayList)
        var amountTotal = 0
        for (multiContactModel: MultiContactModel in arrayList) {
            amountTotal += multiContactModel.amount.toInt()
        }
        totalAmount.text = String.format("%s %s", "Kes", NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal))
    }

    fun prevPage() {
        onBackPressed()
    }

    override fun editSendToManyItem(updatedItem: MultiContactModel, position: Int) {
        arrayList.set(position, updatedItem)
        usersAdapter.setContact(arrayList)
        var amountTotal = 0
        for (multiContactModel: MultiContactModel in arrayList) {
            amountTotal += multiContactModel.amount.toInt()
        }
        totalAmount.text = String.format("%s %s", "Kes", NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal))
    }

    override fun updateItem(item: String, itemPosition: String) {
        val editSendToManyBottomSheet = EditSendToManyBottomSheet();
        val bundle = Bundle()
        bundle.putString("personData", item)
        bundle.putString("itemPosition", itemPosition)
        bundle.putString("title", "Edit Beneficiary ")
        editSendToManyBottomSheet.arguments = bundle
        editSendToManyBottomSheet.show(supportFragmentManager, "Edit Contact");
    }

    override fun enterPinDialog(pin: String) {
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        val provider = if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "")!!.contains(SEND_MONEY_TO_ROUTE)) {
            "ROUTEWALLET"
        } else {
            "MPESA PAYBILL"
        }
        token = "Bearer " + prefs.getString(USER_TOKEN, "")
        var filteredList = ArrayList<MultiContactModel>()
        for (item in arrayList) {
            if (item.amount.toInt() > 0) {
                filteredList.add(item)
            }
        }

        if (filteredList.isNotEmpty()) {
            jsonn = gsonn.toJson(filteredList)
            sendToMany(this, pin, provider, "", token, jsonn)
                    .setCallback { e, result ->
                        progressDialog.dismiss()
                        Timber.e("HAPA Error%s", " $e  res $result")
                        if (result.has("errors")) {
                            Toast.makeText(this@SendToManyActivity, result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                        } else
                            if (result["status"].toString().contains("success")) {
                                messagetxt = result["data"].asJsonObject.get("message").asString
                                if (prefs.getString(GROUP_IS_SAVED, "")!!.contains("YES")) {
                                    val intent = Intent(this, FundRequestedActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("Message", messagetxt)
                                    startActivity(intent)
                                    finish()
                                    return@setCallback
                                }

                                val enterGroupBottomSheet = EnterGroupBottomSheet(messagetxt)
                                enterGroupBottomSheet.show(supportFragmentManager, "Save Group")
                            }
                    }
        } else {
            progressDialog.dismiss()
            Toast.makeText(this, "Amount cannot be zero or negative", Toast.LENGTH_LONG).show()
        }
    }

    override fun enterGroupNameDialog(group: String) {
        jsonn = gsonn.toJson(arrayList)
        saveSendToManyGroup(this, token, jsonn, group).setCallback { e, result ->
            if (result["status"].toString().contains("success")) {
                val intent = Intent(this, FundRequestedActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("Message", messagetxt)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Amount cannot be zero or negative", Toast.LENGTH_LONG).show()
            }
        }
    }
}
