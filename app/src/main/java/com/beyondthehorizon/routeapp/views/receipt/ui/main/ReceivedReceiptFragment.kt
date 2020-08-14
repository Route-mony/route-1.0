package com.beyondthehorizon.routeapp.views.receipt.ui.main


import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.ReceiptAdapter
import com.beyondthehorizon.routeapp.models.ReceiptModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_received_receipt.*
import kotlinx.android.synthetic.main.fragment_received_receipt.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ReceivedReceiptFragment : Fragment() {

    private lateinit var receiptAdapter: ReceiptAdapter
    private lateinit var prefs: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_received_receipt, container, false)

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        receiptAdapter = ReceiptAdapter(requireActivity(), "ReceivedReceiptFragment")

        loadSentReceipts(view)

        // Set an on refresh listener for swipe refresh layout
        view.swipe.setOnRefreshListener {
            // Initialize a new Runnable
            loadSentReceipts(view)
        }

        return view
    }

    private fun loadSentReceipts(view: View) {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Constants.getUserReceipt(activity, token, "received").setCallback { _, result ->
                progressDialog.dismiss()
                if (result != null) {
                    Log.e("ReceivedReceiptFragment", result.toString())

//                    if (result.get("status").asString.toString().compareTo("success") == 0) {
//
                    val list = ArrayList<ReceiptModel>()
                    receiptAdapter.clearList()
                    if (result.get("data").asJsonObject.get("rows").asJsonArray.size() == 0) {
                        return@setCallback
                    }

                    for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
                        val issueObj = JSONObject(item.toString())
                        val iterator: Iterator<String> = issueObj.keys()
                        while (iterator.hasNext()) {
                            val key = iterator.next()

                            Log.e("ReceivedReceipt 134567", key)

                            list.add(ReceiptModel(key, "fname", "lname",
                                    "username", "image", "created_at",
                                    "amount_spent", "title", "description", "cancellation_reason",
                                    "transaction_date", "phone_number", "status", "email", "sent", Constants.RECYCLER_HEADER))

                            val transactionArray = issueObj.getJSONArray(key)
                            for (i in 0 until transactionArray.length()) {
                                val item = transactionArray.getJSONObject(i)
                                Log.e("ReceivedReceipt 134567", item.toString())

                                val fname = item.getJSONObject("sender").get("first_name").toString()
                                val lname = item.getJSONObject("sender").get("last_name").toString()
                                val username = item.getJSONObject("sender").get("username").toString()
                                val email = item.getJSONObject("sender").get("email").toString()
                                val phone_number = item.getJSONObject("sender").get("phone_number").toString()

                                val receipt_id = item.get("id").toString()
                                val created_at = item.get("created_at").toString()
                                val amount_spent = item.get("amount_spent").toString()
                                val title = item.get("title").toString()
                                val description = item.get("description").toString()
                                val status = item.get("status").toString()
                                val image = item.get("image").toString()
                                val cancellation_reason = item.get("cancellation_reason").toString()
                                val transaction_date = item.get("transaction_date").toString()

                                list.add(ReceiptModel(receipt_id, fname, lname,
                                        username, image, created_at,
                                        amount_spent, title,
                                        description, cancellation_reason,
                                        transaction_date, phone_number, status, email, "received", Constants.RECYCLER_SECTION))
                            }
                        }
                    }
//                        for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
//
//                            Log.e("ReceivedReceiptFragment", item.toString())
//
//
////                        var id = item.asJsonObject.get("id").asString
//                            val fname = item.asJsonObject.get("sender").asJsonObject.get("first_name").asString
//                            val lname = item.asJsonObject.get("sender").asJsonObject.get("last_name").asString
//                            val username = item.asJsonObject.get("sender").asJsonObject.get("username").asString
//                            val email = item.asJsonObject.get("sender").asJsonObject.get("email").asString
//                            val phone_number = item.asJsonObject.get("sender").asJsonObject.get("phone_number").asString
//                            val image = item.asJsonObject.get("image").asString
//                            val created_at = item.asJsonObject.get("created_at").asString
//                            val amount_spent = item.asJsonObject.get("amount_spent").asString
//                            val title = item.asJsonObject.get("title").asString
//                            val description = item.asJsonObject.get("description").asString
//                            val status = item.asJsonObject.get("status").asString
//                            val receipt_id = item.asJsonObject.get("id").asString
//                            val cancellation_reason = item.asJsonObject.get("cancellation_reason").asString
//                            val transaction_date = item.asJsonObject.get("transaction_date").asString
//
//                            list.add(ReceiptModel(receipt_id, fname, lname,
//                                    username, image, created_at,
//                                    amount_spent, title,
//                                    description, cancellation_reason,
//                                    transaction_date, phone_number, status, email, "received"))
//                        }
                    receivedRecycler.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = receiptAdapter
                    }
//                        list.reverse()
                    receiptAdapter.setContact(list)
//                    }
                }

                // Hide swipe to refresh icon animation
                view.swipe.isRefreshing = false
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
    }
}
