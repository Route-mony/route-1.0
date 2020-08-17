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
import com.beyondthehorizon.routeapp.models.TransactionModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.RECYCLER_HEADER
import com.beyondthehorizon.routeapp.utils.Constants.RECYCLER_SECTION
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_sent_receipt.*
import kotlinx.android.synthetic.main.fragment_sent_receipt.view.*
import kotlinx.android.synthetic.main.fragment_sent_receipt.view.receivedRecycler
import org.json.JSONObject
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SentReceiptFragment : Fragment() {

    private lateinit var receiptAdapter: ReceiptAdapter
    private lateinit var prefs: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sent_receipt, container, false)

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        receiptAdapter = ReceiptAdapter(requireActivity(), "SentReceiptFragment")
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

            Constants.getUserReceipt(activity, token, "sent").setCallback { _, result ->
                progressDialog.dismiss()
                if (result != null) {

                    Log.e("ReceivedReceipt Sent", result.toString())

//                    if (result.get("status").toString().toString().compareTo("success") == 0) {
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
                                    "transaction_date", "phone_number", "status", "email", "sent", RECYCLER_HEADER))

                            val transactionArray = issueObj.getJSONArray(key)
                            for (i in 0 until transactionArray.length()) {
                                val item = transactionArray.getJSONObject(i)
                                Log.e("ReceivedReceipt 134567", item.toString())

                                val fname = item.getJSONObject("recipient").get("first_name").toString()
                                val lname = item.getJSONObject("recipient").get("last_name").toString()
                                val username = item.getJSONObject("recipient").get("username").toString()
                                val email = item.getJSONObject("recipient").get("email").toString()
                                val phone_number = item.getJSONObject("recipient").get("phone_number").toString()

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
                                        amount_spent, title, description, cancellation_reason,
                                        transaction_date, phone_number, status, email, "sent", RECYCLER_SECTION))
                            }
                        }
                    }

////                            val details = item.asJsonObject.get("sender").toString()
//                            Log.e("ReceivedReceipt22", item.asJsonObject.get("image").toString())
//
//
////                        var id = item.asJsonObject.get("id").toString()
//                            val fname = item.asJsonObject.get("recipient").asJsonObject.get("first_name").toString()
//                            val lname = item.asJsonObject.get("recipient").asJsonObject.get("last_name").toString()
//                            val username = item.asJsonObject.get("recipient").asJsonObject.get("username").toString()
//                            val email = item.asJsonObject.get("recipient").asJsonObject.get("email").toString()
//                            val phone_number = item.asJsonObject.get("recipient").asJsonObject.get("phone_number").toString()
//
//                            val receipt_id = item.asJsonObject.get("id").toString()
//                            val created_at = item.asJsonObject.get("created_at").toString()
//                            val amount_spent = item.asJsonObject.get("amount_spent").toString()
//                            val title = item.asJsonObject.get("title").toString()
//                            val description = item.asJsonObject.get("description").toString()
//                            val status = item.asJsonObject.get("status").toString()
//                            val image = item.asJsonObject.get("image").toString()
//                            val cancellation_reason = item.asJsonObject.get("cancellation_reason").toString()
//                            val transaction_date = item.asJsonObject.get("transaction_date").toString()
//
//                            list.add(ReceiptModel(receipt_id, fname, lname,
//                                    username, image, created_at,
//                                    amount_spent, title,
//                                    description, cancellation_reason,
//                                    transaction_date, phone_number, status, email, "sent"))
//                        }
                    receivedRecycler.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = receiptAdapter
                    }
//                    list.reverse()
                    receiptAdapter.setContact(list)
//                    }
                }
                // Hide swipe to refresh icon animation
                view.swipe.isRefreshing = false
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}