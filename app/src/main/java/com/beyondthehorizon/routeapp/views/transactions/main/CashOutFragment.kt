package com.beyondthehorizon.routeapp.views.transactions.main


import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.TransactionsAdapter
import com.beyondthehorizon.routeapp.models.TransactionModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_cash_out.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class CashOutFragment : Fragment() {

    private lateinit var transactionsAdapter: TransactionsAdapter
    private lateinit var prefs: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cash_out, container, false)

        prefs = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        transactionsAdapter = TransactionsAdapter(activity!!)
        loadSentTransactions()

        return view
    }

    private fun loadSentTransactions() {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            Constants.getUserStatement(activity, token, "cash_outs").setCallback { _, result ->
                progressDialog.dismiss()
                if (result != null) {

                    Log.e("HERE 11 ", result.toString())
                    if (result.get("data").asJsonObject.get("rows").asJsonArray.size() == 0) {
                        return@setCallback
                    }
                    val list = ArrayList<TransactionModel>()
                    transactionsAdapter.clearList()
                    for (item1: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
                        val issueObj = JSONObject(item1.toString())
                        val iterator: Iterator<String> = issueObj.keys()

                        while (iterator.hasNext()) {
                            val key = iterator.next()

                            list.add(TransactionModel(key, "recipient", "withdrawn",
                                    "paymentType", "balance", "wallet_account",
                                    "reference", "description", "email", "image", Constants.RECYCLER_HEADER))
                            val transactionArray = issueObj.getJSONArray(key)

                            for (i in 0 until transactionArray.length()) {
                                val item = transactionArray.getJSONObject(i)
                                if (item.has("recipient")) {
                                    Log.e("HERE 134567 ", item.toString())
                                    val date: String = item.get("date").toString()
                                    val first_name = item.getJSONObject("recipient").get("first_name").toString()
                                    val last_name = item.getJSONObject("recipient").get("last_name").toString()
                                    val time = item.get("time").toString()
                                    val withdrawn = item.get("received").toString()
                                    val paymentType = "received"
                                    val email = item.getJSONObject("recipient").get("email").toString()
                                    val image = item.getJSONObject("recipient").get("image").toString()
                                    val recipient = "$first_name $last_name"
                                    val description = item.get("description").toString()
                                    val balance = item.get("balance").toString()
                                    val wallet_account = item.get("wallet_account").toString()
                                    val reference = item.get("reference").toString()

                                    Log.e("HERE 134 ", last_name)

                                    val created_at = "$date  $time"
                                    list.add(TransactionModel(created_at, recipient, withdrawn,
                                            paymentType, balance, wallet_account, reference, description, email, image, Constants.RECYCLER_SECTION))
                                }
                            }
                        }
                    }
//                        for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
////                        var id = item.asJsonObject.get("id").asString
////                        var username = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString + " " +
////                                item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
////                        var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
////                        var imageUrl = R.drawable.group416
//                            val date = item.asJsonObject.get("date").asString
//                            val time = item.asJsonObject.get("time").asString
//                            val first_name = item.asJsonObject.get("recipient").asJsonObject.get("first_name").asString
//                            val last_name = item.asJsonObject.get("recipient").asJsonObject.get("last_name").asString
//                            val email = item.asJsonObject.get("recipient").asJsonObject.get("email").asString
//                            val image = item.asJsonObject.get("recipient").asJsonObject.get("image").asString
//
//                            val recipient = "$first_name $last_name"
//                            val withdrawn = item.asJsonObject.get("cash_outs").asString
//                            val paymentType = "cash_outs"
//                            val balance = item.asJsonObject.get("balance").asString
//                            val description = item.asJsonObject.get("description").asString
//                            val wallet_account = item.asJsonObject.get("wallet_account").asString
//                            val reference = item.asJsonObject.get("reference").asString
////                        var status = item.asJsonObject.get("status").asString.toLowerCase()
////                        var statusIcon = statusMapper[status]
//                            val created_at = "$date  $time"
//
//                            list.add(TransactionModel(created_at, recipient, withdrawn, paymentType,
//                                    balance, wallet_account, reference, description, email, image))
//                        }
                    cashOutRecycler.apply {
                        layoutManager = LinearLayoutManager(activity!!)
                        adapter = transactionsAdapter
                    }
                    transactionsAdapter.setContact(list)
//                    }
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
    }
}