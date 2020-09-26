package com.beyondthehorizon.route.views

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.BulkyRequestAdapter
import com.beyondthehorizon.route.bottomsheets.AddBulkRequestItemBottomSheet
import com.beyondthehorizon.route.models.BulkyRequestModel
import com.beyondthehorizon.route.utils.Constants
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

    override fun onResume() {
        arrayList.clear()
        arrayListJson.clear()
        arrayList.add(BulkyRequestModel("Reason/Item", "Unit Price", "Quantity"))
        usersAdapter.setContact(arrayList)
        super.onResume()
    }
}
