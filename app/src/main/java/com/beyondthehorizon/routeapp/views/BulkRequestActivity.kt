package com.beyondthehorizon.routeapp.views

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
import com.beyondthehorizon.routeapp.adapters.BulkyRequestAdapter
import com.beyondthehorizon.routeapp.bottomsheets.AddBulkRequestItemBottomSheet
import com.beyondthehorizon.routeapp.bottomsheets.TransactionModel
import com.beyondthehorizon.routeapp.models.BulkyRequestModel
import com.beyondthehorizon.routeapp.utils.Constants
import kotlinx.android.synthetic.main.activity_bulk_request.*

val arrayList = ArrayList<BulkyRequestModel>()
val arrayListJson = ArrayList<String>()
lateinit var usersAdapter: BulkyRequestAdapter

class BulkRequestActivity : AppCompatActivity(), AddBulkRequestItemBottomSheet.AddBulkRequestItemBottomSheetListener {
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulk_request)

        usersAdapter = BulkyRequestAdapter(this@BulkRequestActivity) { position ->
            run {
                arrayListJson.removeAt(position - 1)
                var amountTotal = 0
                if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
                    for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                        amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
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
        arrayList.add(BulkyRequestModel("Reason/Item", "Unit Price", "Quantity"))
        usersAdapter.setContact(arrayList)

        addFab.setOnClickListener {
            val addBulkRequestItemBottomSheet = AddBulkRequestItemBottomSheet();
            addBulkRequestItemBottomSheet.show(getSupportFragmentManager(), "AddBulk RequestItem");

            //Inflate the dialog with custom view
//            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_bulk_item_layout, null)
//            //AlertDialogBuilder
//            val mBuilder = AlertDialog.Builder(this)
//                    .setView(mDialogView)
//            //show dialog
//            val mAlertDialog = mBuilder.show()
//            //login button click of custom layout
//            mDialogView.dialogAddBtn.setOnClickListener {
//                //dismiss dialog
//                mAlertDialog.dismiss()
//                //get text from EditTexts of custom layout
//                val name = mDialogView.itemName.text.toString().trim()
//                val quantity = mDialogView.itemQuantity.text.toString().trim()
//                val amount = mDialogView.itemAmount.text.toString().trim()
//                //set the input text in TextView
//                if (name.isEmpty()) {
//                    mDialogView.itemName.error = "Cannot be empty"
//                    return@setOnClickListener
//                }
//                if (amount.isEmpty()) {
//                    mDialogView.itemName.error = "Cannot be empty"
//                    return@setOnClickListener
//                }
//                var amountTotal = 0
//                val item = JsonObject()
//                item.addProperty("title", name)
//                item.addProperty("unit_price", amount)
//                item.addProperty("quantity", quantity)
//                arrayList.add(BulkyRequestModel(name, amount, quantity))
//                arrayListJson.add(item.toString())
//                usersAdapter.setContact(arrayList)
//                mAlertDialog.dismiss()
//                if (arrayList.size > 1) {
////                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
//                    for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
//                        amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
//                        Log.e("NAHAPA", amountTotal.toString())
//                    }
//                    totals.text = amountTotal.toString()
//                    totalCard.visibility = View.VISIBLE
//                    emptyList.visibility = View.GONE
//                }
////                else if (arrayList.size == 2) {
////                    totals.text = arrayList.get(1).amount
////                    totalCard.visibility = View.VISIBLE
////
////                }
//
//            }
        }
        requestButton.setOnClickListener {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            val progressDialog = ProgressDialog(this@BulkRequestActivity)
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Constants.bulkRequest(this@BulkRequestActivity, token, "NO from designation",
                    "NO from department", "NO project title", "NO to designation",
                    "NO to department", prefs.getString("Id", "").toString(), arrayListJson.toString())
                    .setCallback { e, result ->
                        progressDialog.dismiss()
                        if (e != null) {
                            Log.e("BulkRequestActivity", e.toString())
                        }
                        Log.e("BulkRequestActivity", result.toString())
                        Toast.makeText(this@BulkRequestActivity, "Request send successfully", Toast.LENGTH_LONG).show()
//                        val intent = Intent(this@BulkRequestActivity, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent) val message = result.get("data").asJsonObject.get("message").asString
                        val intent = Intent(this@BulkRequestActivity, FundRequestedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("Message", "Request send successfully")
                        startActivity(intent)
                    }
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun addNewBulkRequestItem(arrayList2: ArrayList<BulkyRequestModel>, arrayListJson2: ArrayList<String>) {
        var amountTotal = 0
        arrayListJson.addAll(arrayListJson2)
        arrayList.addAll(arrayList2)
        if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
            for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
//                Log.e("NAHAPA", amountTotal.toString())
            }
            usersAdapter.setContact(arrayList)
            totals.text = amountTotal.toString()
            totalCard.visibility = View.VISIBLE
            emptyList.visibility = View.GONE
        }
    }
}
