package com.beyondthehorizon.routeapp.views.receipt.ui.main


import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.ReceiptAdapter
import com.beyondthehorizon.routeapp.models.ReceiptModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.NetworkUtils
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_received_receipt.*
import kotlinx.android.synthetic.main.fragment_received_receipt.view.*
import org.json.JSONObject
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ReceivedReceiptFragment : Fragment() {

    private lateinit var receiptAdapter: ReceiptAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var networkUtils: NetworkUtils
    private lateinit var tvNoInternet: TextView
    private lateinit var rvReceived: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_received_receipt, container, false)
        tvNoInternet = view.findViewById(R.id.tvNoInternet)
        rvReceived = view.findViewById(R.id.receivedRecycler)
        networkUtils = NetworkUtils(requireContext())
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
            if (networkUtils.isNetworkAvailable) {
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
                        receivedRecycler.apply {
                            layoutManager = LinearLayoutManager(activity)
                            adapter = receiptAdapter
                        }
                        receiptAdapter.setContact(list)
                    }

                    view.swipe.isRefreshing = false
                }
            } else {
                tvNoInternet.visibility = View.VISIBLE
                rvReceived.visibility = View.GONE
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}
