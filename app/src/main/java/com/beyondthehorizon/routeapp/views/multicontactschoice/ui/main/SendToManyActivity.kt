package com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.routeapp.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.routeapp.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.routeapp.models.MultiContactModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_confirm_pin.view.*
import kotlinx.android.synthetic.main.activity_confirm_pin.view.dialogButtonOK
import kotlinx.android.synthetic.main.activity_send_to_many.*
import kotlinx.android.synthetic.main.group_name_item.view.*
import timber.log.Timber
import java.lang.reflect.Type
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SendToManyActivity : AppCompatActivity(), EditSendToManyBottomSheet.EditSendToManyBottomSheetListener, GroupSendToManyAdapter.SendToManyInterface,
        EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener {

    var arrayList = ArrayList<MultiContactModel>()
    val arrayListJson = ArrayList<String>()
    lateinit var usersAdapter: GroupSendToManyAdapter
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_to_many)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()

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
        arrayList.add(MultiContactModel("id", "Name"
                , "Quantity", "Quantity", "Amount", is_route = false, is_selected = false))
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
        editSendToManyBottomSheet.arguments = bundle
        editSendToManyBottomSheet.show(supportFragmentManager, "Edit Contact");
    }

    override fun enterPinDialog(pin: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        val provider = if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "")!!.contains(SEND_MONEY_TO_ROUTE)) {
            "ROUTEWALLET"
        } else {
            "MPESA PAYBILL"
        }
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
        val gsonn = Gson()
        val jsonn: String = gsonn.toJson(arrayList)

        sendToMany(this, pin, provider, "", token, jsonn)
                .setCallback { e, result ->
                    progressDialog.dismiss()
                    Timber.e("HAPA Error%s", " $e  res $result")
                    if (result["status"].toString().contains("failed")) {
                        Toast.makeText(this, result["errors"].toString(), Toast.LENGTH_LONG).show()
                    } else
                        if (result["status"].toString().contains("success")) {
                            val messagetxt = result["data"].asJsonObject.get("message").asString

                            if (prefs.getString(GROUP_IS_SAVED, "")!!.contains("YES")) {
                                val intent = Intent(this, FundRequestedActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("Message", messagetxt)
                                startActivity(intent)
                                finish()
                                return@setCallback
                            }
                            val mDialogView = LayoutInflater.from(this).inflate(R.layout.group_name_item, null)
                            val mBuilder = AlertDialog.Builder(this)
                                    .setView(mDialogView)

                            //show dialog
                            val mAlertDialog = mBuilder.show()
                            mAlertDialog.setCancelable(false)
                            mDialogView.dialogButtonOKk.setOnClickListener {
                                val grName = mDialogView.group_name.text.toString()
                                if (grName.isBlank()) {
                                    mDialogView.group_name.error = "Cannot be empty"
                                    return@setOnClickListener
                                }
                                progressDialog.show()
                                saveSendToManyGroup(this, token, jsonn, grName).setCallback { e, result ->
                                    progressDialog.dismiss()
                                    if (result["status"].toString().contains("success")) {
                                        mAlertDialog.dismiss()
                                        val intent = Intent(this, FundRequestedActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        intent.putExtra("Message", messagetxt)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        mAlertDialog.dismiss()
                                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            mDialogView.dialogButtonNoo.setOnClickListener {
                                mAlertDialog.dismiss()
                                val intent = Intent(this, FundRequestedActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("Message", messagetxt)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
    }
}
