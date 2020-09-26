package com.beyondthehorizon.route.views.multicontactschoice

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.SavedGroupsAdapter
import com.beyondthehorizon.route.models.MultiContactModel
import com.beyondthehorizon.route.models.ReceiptModel
import com.beyondthehorizon.route.models.SavedGroupItem
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_send_to_many_groups.*
import kotlinx.android.synthetic.main.fragment_received_receipt.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class SendToManyGroupsActivity : AppCompatActivity() {
    private lateinit var savedGroupsAdapter: SavedGroupsAdapter
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_to_many_groups)

        newGroup.setOnClickListener {
            startActivity(Intent(this, MultiContactsActivity::class.java))
        }
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
        savedGroupsAdapter = SavedGroupsAdapter(this);
        getSendToManyGroup(this, token).setCallback { e, result ->
            Timber.e("SASA %s", result)
            if (result != null) {
                val list = ArrayList<SavedGroupItem>()
                savedGroupsAdapter.clearList()
                if (result.get("data").asJsonObject.get("rows").asJsonArray.size() == 0) {
                    return@setCallback
                }
                for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
                    val issueObj = JSONObject(item.toString())
                    val iterator: Iterator<String> = issueObj.keys()
                    while (iterator.hasNext()) {
                        val key = iterator.next()
                        Log.e("ReceivedReceipt 134567", key)
                        list.add(SavedGroupItem(key, "", "", "", RECYCLER_HEADER))

                        val transactionArray = issueObj.getJSONArray(key)
                        for (i in 0 until transactionArray.length()) {
                            val item2 = transactionArray.getJSONObject(i)
                            Log.e("ReceivedReceipt 134567", item2.toString())

                            val iterator2: Iterator<String> = item2.keys()
                            while (iterator2.hasNext()) {
                                val key2 = iterator2.next()
                                val groupArray = item2.getJSONObject(key2)
                                Log.e("ReceivedReceipt Group", groupArray.toString())

                                val recipient = groupArray.getString("recipients").toString()
                                val groupName = groupArray.getString("group_name").toString()
                                val total_amount = groupArray.getString("total_amount").toString()

//                                Log.e("HAPAKESHO", recipient)
                                list.add(SavedGroupItem(recipient, "Pending", groupName, total_amount, RECYCLER_SECTION))
                            }
                        }
                    }
                }
                groupsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@SendToManyGroupsActivity)
                    adapter = savedGroupsAdapter
                }
                savedGroupsAdapter.setContact(list)
            }
        }

    }

    fun prevPage(view: View) {
        onBackPressed()
    }
}