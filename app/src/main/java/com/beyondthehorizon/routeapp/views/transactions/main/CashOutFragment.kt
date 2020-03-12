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
            Constants.getUserStatement(activity, token).setCallback { e, result ->
                progressDialog.dismiss()
                if (result != null) {
                    Log.e("HERE", result.get("data").asJsonObject.get("rows").asJsonArray.toString())

                    val list = ArrayList<TransactionModel>()
                    transactionsAdapter.clearList()
                    for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
//                        var id = item.asJsonObject.get("id").asString
//                        var username = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString + " " +
//                                item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
//                        var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
//                        var imageUrl = R.drawable.group416
                        val created_at = item.asJsonObject.get("created_at").asString
                        val details = item.asJsonObject.get("details").asString
                        val withdrawn = "Ksh. ${item.asJsonObject.get("withdrawn").asString}"
                        val paid_in = item.asJsonObject.get("paid_in").asString
                        val balance = item.asJsonObject.get("balance").asString
                        val wallet_account = item.asJsonObject.get("wallet_account").asString
                        val reference = item.asJsonObject.get("reference").asString
//                        var status = item.asJsonObject.get("status").asString.toLowerCase()
//                        var statusIcon = statusMapper[status]

                        list.add(TransactionModel(created_at, details, withdrawn,
                                paid_in, balance, wallet_account, reference))
                    }
                    cashOutRecycler.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = transactionsAdapter
                    }
                    transactionsAdapter.setContact(list)
                } else {
                    Toast.makeText(activity, "No statement found", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
    }
}