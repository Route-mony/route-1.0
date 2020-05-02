package com.beyondthehorizon.routeapp.views

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.BulkyRequestAdapter
import com.beyondthehorizon.routeapp.models.BulkyRequestModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_bulk_request.*
import kotlinx.android.synthetic.main.add_bulk_item_layout.view.*
import java.util.HashMap

class BulkRequestActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulk_request)

        val arrayList = ArrayList<BulkyRequestModel>()
        val arrayListJson = ArrayList<String>()
        val usersAdapter = BulkyRequestAdapter(this@BulkRequestActivity) { position ->
            run {
                arrayListJson.removeAt(position - 1)
                var amountTotal = 0
                if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
                    for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                        amountTotal += bulkyRequestModel.amount.toInt()
                    }
                    totals.text = amountTotal.toString()
                    totalCard.visibility = View.VISIBLE
                    emptyList.visibility = View.GONE
                } else {
                    totalCard.visibility = View.GONE
                    emptyList.visibility = View.VISIBLE
                }

            }
        }
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
        arrayList.add(BulkyRequestModel("Reason/Item", "Amount", "Quantity"))
        usersAdapter.setContact(arrayList)

        addFab.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_bulk_item_layout, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
            //show dialog
            val mAlertDialog = mBuilder.show()
            //login button click of custom layout
            mDialogView.dialogAddBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val name = mDialogView.itemName.text.toString().trim()
                val quantity = mDialogView.itemQuantity.text.toString().trim()
                val amount = mDialogView.itemAmount.text.toString().trim()
                //set the input text in TextView
                if (name.isEmpty()) {
                    mDialogView.itemName.error = "Cannot be empty"
                    return@setOnClickListener
                }
                if (amount.isEmpty()) {
                    mDialogView.itemName.error = "Cannot be empty"
                    return@setOnClickListener
                }
                var amountTotal = 0
                val item = JsonObject()
                item.addProperty("title", name)
                item.addProperty("unit_price", amount)
                item.addProperty("quantity", quantity)
                arrayList.add(BulkyRequestModel(name, amount, quantity))
                arrayListJson.add(item.toString())
                usersAdapter.setContact(arrayList)
                mAlertDialog.dismiss()
                if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
                    for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                        amountTotal += bulkyRequestModel.amount.toInt()
                    }
                    totals.text = amountTotal.toString()
                    totalCard.visibility = View.VISIBLE
                    emptyList.visibility = View.GONE
                }
//                else if (arrayList.size == 2) {
//                    totals.text = arrayList.get(1).amount
//                    totalCard.visibility = View.VISIBLE
//
//                }
            }
        }
        requestButton.setOnClickListener {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            val progressDialog = ProgressDialog(this@BulkRequestActivity)
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            var from_user: HashMap<String, JsonObject> = hashMapOf()
            var to_user: HashMap<String, JsonObject> = hashMapOf()
            val from_user_json = JsonObject()
            from_user_json.addProperty("designation", "NO from user")
            from_user_json.addProperty("department", "NO from department")
            from_user_json.addProperty("project_title", "NO from project_title")

            from_user.put("from_user", from_user_json)

            val to_user_json = JsonObject()
            to_user_json.addProperty("designation", "NOn3 to user")
            to_user_json.addProperty("department", "No department to user")
            to_user_json.addProperty("recipient", prefs.getString("Id", "").toString())
            to_user.put("to_user", to_user_json)

            Constants.bulkRequest(this@BulkRequestActivity, token, from_user, to_user, arrayListJson)
                    .setCallback { e, result ->
                        progressDialog.dismiss()
                        if (e != null) {
                            Log.e("BulkRequestActivity", e.toString())
                        }
                        Log.e("BulkRequestActivity", result.toString())

                    }
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }
}
