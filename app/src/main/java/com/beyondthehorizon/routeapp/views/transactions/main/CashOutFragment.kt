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

                Log.e("HERE 13 ", result.get("data").asJsonObject.get("rows").asJsonArray.toString())

                if (result != null) {

                    Log.e("HERE 11 ", result.toString())
//                    Log.e("HERE", result.get("data").asJsonObject.get("rows").asJsonArray.toString())
//
                    if (result.get("status").asString.toString().compareTo("success") == 0) {
                        if (result.get("data").asJsonObject.get("rows").asJsonArray.size() == 0) {
                            return@setCallback
                        }
                        val list = ArrayList<TransactionModel>()
                        transactionsAdapter.clearList()
                        for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
//                        var id = item.asJsonObject.get("id").asString
//                        var username = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString + " " +
//                                item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
//                        var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
//                        var imageUrl = R.drawable.group416
                            val date = item.asJsonObject.get("date").asString
                            val time = item.asJsonObject.get("time").asString
                            val recipient = item.asJsonObject.get("recipient").asString
                            val withdrawn = item.asJsonObject.get("cash_outs").asString
                            val paymentType = "cash_outs"
                            val balance = item.asJsonObject.get("balance").asString
                            val description = item.asJsonObject.get("description").asString
                            val wallet_account = item.asJsonObject.get("wallet_account").asString
                            val reference = item.asJsonObject.get("reference").asString
//                        var status = item.asJsonObject.get("status").asString.toLowerCase()
//                        var statusIcon = statusMapper[status]
                            val created_at = "$date  $time"

                            list.add(TransactionModel(created_at, recipient, withdrawn,
                                    paymentType, balance, wallet_account, reference, description))
                        }
                        cashOutRecycler.apply {
                            layoutManager = LinearLayoutManager(activity!!)
                            adapter = transactionsAdapter
                        }
                        transactionsAdapter.setContact(list)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
    }
}