package com.beyondthehorizon.route.views.fragments.services.groups.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.adapters.SavedGroupsAdapter
import com.beyondthehorizon.route.databinding.FragmentSendToManyGroupsBinding
import com.beyondthehorizon.route.interfaces.IOnGroupSelected
import com.beyondthehorizon.route.models.SavedGroupItem
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.models.contacts.MultiContactModels
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.services.groups.MultiContactsFragment
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.json.JSONObject


class SendToManyGroupsFragment(private val data: TransactionData? = null) : BaseFragment(),
    IOnGroupSelected {
    private var _binding: FragmentSendToManyGroupsBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedGroupsAdapter: SavedGroupsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendToManyGroupsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        transactionData = data!!
        savedGroupsAdapter = SavedGroupsAdapter();

        binding.newGroup.setOnClickListener {
            replaceFragment(MultiContactsFragment(transactionData))
        }
        fetchGroups()
        return view
    }

    private fun fetchGroups() {
        Constants.getSendToManyGroup(requireContext(), SharedPref.getToken(requireContext()))
            .setCallback { e, result ->
                if (e != null) {
                    showCustomToast(requireContext(), "group(s)", 0)
                    return@setCallback
                }

                if (result != null) {
                    val list = ArrayList<SavedGroupItem>()
                    if (result.get("data").asJsonObject.get("rows").asJsonArray.size() == 0) {
                        return@setCallback
                    }
                    for (item: JsonElement in result.get("data").asJsonObject.get("rows").asJsonArray) {
                        val issueObj = JSONObject(item.toString())
                        val iterator: Iterator<String> = issueObj.keys()
                        while (iterator.hasNext()) {
                            val key = iterator.next()

                            val transactionArray = issueObj.getJSONArray(key)
                            for (i in 0 until transactionArray.length()) {
                                val item2 = transactionArray.getJSONObject(i)
                                val iterator2: Iterator<String> = item2.keys()
                                while (iterator2.hasNext()) {
                                    val key2 = iterator2.next()
                                    val groupArray = item2.getJSONObject(key2)
                                    val recipients = Gson().fromJson(
                                        groupArray.getString("group_name"),
                                        MultiContactModels::class.java
                                    )
                                    val groupName = groupArray.getString("group_name").toString()
                                    val total_amount =
                                        groupArray.getString("total_amount").toString()
                                    list.add(
                                        SavedGroupItem(
                                            recipients,
                                            "Pending",
                                            groupName,
                                            total_amount,
                                            Constants.RECYCLER_SECTION
                                        )
                                    )
                                }
                            }
                        }
                    }
                    binding.groupsRecyclerView.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = savedGroupsAdapter
                    }
                    savedGroupsAdapter.setContact(list)
                }
            }
    }

    override fun onGroupSelectedListener(group: SavedGroupItem) {
        replaceFragment(SendToManyFragment(transactionData, group))
    }
}