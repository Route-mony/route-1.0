package com.beyondthehorizon.route.views.split.bill

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.SplitBillGroupsAdapter
import com.beyondthehorizon.route.databinding.ActivitySplitBillBinding
import com.beyondthehorizon.route.models.BillResponse
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.FundAmountActivity
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_split_bill.*
import org.json.JSONObject

class SplitBillActivity : AppCompatActivity() {
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences
    private lateinit var payload: MutableList<BillResponse>
    private lateinit var mAdapter: SplitBillGroupsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivitySplitBillBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_split_bill)
        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        payload = mutableListOf()
        fetchBills()

        rlNewSplitBill.setOnClickListener {
            var intent = Intent(this, FundAmountActivity::class.java)
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SPLIT_BILL)
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE)
            editor.apply()
            startActivity(intent)
        }

        arrow_back.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun fetchBills() {
        val token = "Bearer " + prefs.getString(USER_TOKEN, "")
        getSplitBillGroups(this, "sent", token).setCallback { e, result ->
            try {
                if (result.has("data")) {

                    for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
                        val issueObj = JSONObject(item.toString())
                        val iterator: Iterator<String> = issueObj.keys()
                        while (iterator.hasNext()) {
                            val key = iterator.next()
                            Log.e("TAG", key)
                            val transactionArray = issueObj.getJSONArray(key)
                            for (i in 0 until transactionArray.length()) {
                                val item2 = transactionArray.getJSONObject(i)
                                Log.e("ReceivedReceipt 134567", item2.toString())

                                val iterator2: Iterator<String> = item2.keys()
                                while (iterator2.hasNext()) {
                                    val key2 = iterator2.next()
                                    val data = item2.getJSONObject(key2)
                                    val billId = data.getString("split_bill_id")
                                    val group = data.getString("reason")
                                    val amount = data.getString("total_requested")
                                    val status = data.getString("general_status")
                                    val recipients = data.getJSONArray("bills")
                                    payload.add(BillResponse(billId, group, amount, status, recipients, key))
                                    Log.d("RECIPIENTS", "ID $billId \nGroup $group \nAmount $amount \nStatus $status \nRecipients $recipients")
                                }
                            }
                        }
                    }
                    layoutManager = LinearLayoutManager(this)
                    binding.groupsRecyclerView.layoutManager = this.layoutManager
                    mAdapter = SplitBillGroupsAdapter(this, payload)
                    binding.groupsRecyclerView.adapter = mAdapter
                    Log.d("BILLS: ", payload.size.toString())
                }
                else{
                    Toast.makeText(this, "Unable to fetch split groups", Toast.LENGTH_LONG)
                }
            }
            catch (ex: Exception){
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }
    }
}
