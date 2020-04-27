package com.beyondthehorizon.routeapp.views

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.BulkyRequestAdapter
import com.beyondthehorizon.routeapp.models.BulkyRequestModel
import kotlinx.android.synthetic.main.activity_bulk_request.*
import kotlinx.android.synthetic.main.add_bulk_item_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class BulkRequestActivity : AppCompatActivity() {
    private val usersAdapter = BulkyRequestAdapter(this@BulkRequestActivity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulk_request)

        bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
        val arrayList = ArrayList<BulkyRequestModel>()
        arrayList.add(BulkyRequestModel("Reason/Item", "Quantity", "Amount"))
        usersAdapter.setContact(arrayList)

        addFab.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_bulk_item_layout, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
            //show dialog
            val mAlertDialog = mBuilder.show()
            var amountTotal = 0
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
                arrayList.add(BulkyRequestModel(name, quantity, amount))
                usersAdapter.setContact(arrayList)
                mAlertDialog.dismiss()
                if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
                    for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                        amountTotal += bulkyRequestModel.amount.toInt()
                    }
                    totals.text = amountTotal.toString()
                    totalCard.visibility = View.VISIBLE
                }
//                else if (arrayList.size == 2) {
//                    totals.text = arrayList.get(1).amount
//                    totalCard.visibility = View.VISIBLE
//
//                }
            }
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }
}
