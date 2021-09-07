package com.beyondthehorizon.route.views.fragments.services.groups.split

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.adapters.SplitBillGroupsAdapter
import com.beyondthehorizon.route.databinding.FragmentSplitBillGroupsBinding
import com.beyondthehorizon.route.interfaces.OnSplitBillSelectedListener
import com.beyondthehorizon.route.models.BillResponse
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.SPLIT_BILL
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment
import com.beyondthehorizon.route.views.fragments.services.common.AmountFragment
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.json.JSONObject

class SplitBillGroupsFragment : BaseFragment(), OnSplitBillSelectedListener {
    private var _binding: FragmentSplitBillGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplitBillGroupsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        transactionData.process = SPLIT_BILL
        binding.rlNewSplitBill.setOnClickListener {
            replaceFragment(AmountFragment(transactionData))
        }
        binding.arrowBack.setOnClickListener {
            replaceFragment(HomeFragment())
        }
        fetchBills()
        return view
    }

    private fun fetchBills() {
        val bills = mutableListOf<BillResponse>()
        Constants.getSplitBillGroups(
            requireContext(),
            "sent",
            SharedPref.getToken(requireContext())
        ).setCallback { e, result ->
            try {
                if (result.has("data")) {
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
                                    val billResponse = Gson().fromJson(
                                        item2.getString(key2),
                                        BillResponse::class.java
                                    )
                                    bills.add(billResponse)
                                }
                            }
                        }
                    }
                    binding.groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.groupsRecyclerView.adapter =
                        SplitBillGroupsAdapter(requireContext(), bills, this)
                } else {
                    showCustomToast(requireContext(), "split bill groups", 0)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectedSplitBill(billResponse: BillResponse) {
        replaceFragment(SplitBillFragment(billResponse, transactionData))
    }
}