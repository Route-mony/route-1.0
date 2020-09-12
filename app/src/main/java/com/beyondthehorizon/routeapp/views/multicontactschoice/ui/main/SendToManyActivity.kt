package com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.routeapp.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.routeapp.bottomsheets.EnterGroupBottomSheet
import com.beyondthehorizon.routeapp.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.routeapp.models.MultiContactModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
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
    private lateinit var token:String
    private lateinit var jsonn: String
    private lateinit var messagetxt: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_to_many)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        progressDialog = ProgressDialog(this)

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

        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
        arrayList = gson.fromJson(prefs.getString(MY_MULTI_CHOICE_SELECTED_CONTACTS, ""), type)
        usersAdapter.setContact(arrayList)
        var amountTotal = 0
        for (multiContactModel: MultiContactModel in arrayList) {
            amountTotal += multiContactModel.amount.toInt()
        }
        totalAmount.text = "Kes ${NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)}"
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
        totalAmount.text = "Kes ${NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)}"
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

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        val provider = if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "")!!.contains(SEND_MONEY_TO_ROUTE)) {
            "ROUTEWALLET"
        } else {
            "MPESA PAYBILL"
        }
        token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
        val gsonn = Gson()
        jsonn = gsonn.toJson(arrayList)

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
                }

    override fun enterGroupNameDialog(group: String) {
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
