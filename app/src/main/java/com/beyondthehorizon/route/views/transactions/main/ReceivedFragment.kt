package com.beyondthehorizon.route.views.transactions.main


import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.TransactionsAdapter
import com.beyondthehorizon.route.models.TransactionModel
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.RECYCLER_HEADER
import com.beyondthehorizon.route.utils.Constants.RECYCLER_SECTION
import com.beyondthehorizon.route.utils.NetworkUtils
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_received.*
import org.json.JSONObject
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class ReceivedFragment : Fragment() {
    private lateinit var transactionsAdapter: TransactionsAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var networkUtils: NetworkUtils
    private lateinit var tvNoInternet: TextView
    private lateinit var rvReceived: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_received, container, false)
        tvNoInternet = view.findViewById(R.id.tvNoInternet)
        rvReceived = view.findViewById(R.id.receivedRecycler)
        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        transactionsAdapter = TransactionsAdapter(requireActivity())
        networkUtils = NetworkUtils(requireContext())

        loadSentTransactions()

        return view
    }

    private fun loadSentTransactions() {
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        Constants.getUserStatement(activity, token, "received").setCallback { _, result ->
            progressDialog.dismiss()
            try {
                if (networkUtils.isNetworkAvailable) {
                    if (result != null) {
                        if (result.has("errors")) {
                            Toast.makeText(requireContext(), result.get("errors").asJsonArray.get(0).asString, Toast.LENGTH_LONG).show()
                            return@setCallback
                        }

                        Log.e("HERE 13 ", result.toString())

                        val list = ArrayList<TransactionModel>()
                        transactionsAdapter.clearList()
                        if (result.get("data").asJsonObject.get("rows").asJsonArray.size() == 0) {
                            return@setCallback
                        }
                        for (item1: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
                            val issueObj = JSONObject(item1.toString())
                            val iterator: Iterator<String> = issueObj.keys()

                            while (iterator.hasNext()) {
                                val key = iterator.next()

                                list.add(TransactionModel(key, "sender", "withdrawn",
                                        "paymentType", "balance", "wallet_account",
                                        "reference", "description", "email", "image", RECYCLER_HEADER))
                                val transactionArray = issueObj.getJSONArray(key)

                                for (i in 0 until transactionArray.length()) {
                                    val item = transactionArray.getJSONObject(i)
                                    if (item.has("sender")) {
                                        Log.e("HERE 134 ", item.toString())
                                        val date: String = item.get("date").toString()
                                        val first_name = item.getJSONObject("sender").get("first_name").toString()
                                        val last_name = item.getJSONObject("sender").get("last_name").toString()
                                        val time = item.get("time").toString()
                                        val withdrawn = item.get("received").toString()
                                        val paymentType = "received"
                                        val email = item.getJSONObject("sender").get("email").toString()
                                        val image = item.getJSONObject("sender").get("image").toString()
                                        val sender = "$first_name $last_name"
                                        val description = item.get("description").toString()
                                        val balance = item.get("balance").toString()
                                        val wallet_account = item.get("wallet_account").toString()
                                        val reference = item.get("reference").toString()
                                        Log.e("HERE 134 ", last_name)

                                        val created_at = "$date  $time"
                                        list.add(TransactionModel(created_at, sender, withdrawn,
                                                paymentType, balance, wallet_account, reference, description, email, image, RECYCLER_SECTION))
                                    }
                                }
                            }

                        }
                        receivedRecycler.apply {
                            layoutManager = LinearLayoutManager(activity)
                            adapter = transactionsAdapter
                        }
                        transactionsAdapter.setContact(list)
//                    }
                    }
                } else {
                    tvNoInternet.visibility = View.VISIBLE
                    rvReceived.visibility = View.GONE
                }
            } catch (e: Exception) {
                Timber.d(e.message.toString())
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}