package com.beyondthehorizon.route.views.fragments.services.groups.split

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.route.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.route.databinding.FragmentSplitBillBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.interfaces.bottomsheets.EditSendToManyBottomSheetListener
import com.beyondthehorizon.route.models.BillResponse
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.models.contacts.MultiContactModel
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SplitBillFragment(
    private val billResponse: BillResponse? = null,
    private val data: TransactionData? = null
) : BaseFragment(), IDone, EditSendToManyBottomSheetListener {
    private var _binding: FragmentSplitBillBinding? = null
    private val binding get() = _binding!!
    var arrayList = ArrayList<MultiContactModel>()
    lateinit var usersAdapter: GroupSendToManyAdapter
    private lateinit var format: NumberFormat
    private var amountTotal: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplitBillBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        format = DecimalFormat("#,###")
        transactionData = data!!
        usersAdapter = GroupSendToManyAdapter(requireContext(), this)
        binding.bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }

        binding.tvBillAmount.text =
            String.format("Kes %s", format.format(billResponse!!.totalRequested))
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.back.setOnClickListener {
            replaceFragment(SplitBillGroupsFragment())
        }

        binding.btnSplit.setOnClickListener {
            progressDialog.show()
            try {
                billResponse.bills!!.forEach { arrayList.add(it.recipient) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            if (arrayList.size > 0) {
                Constants.splitBill(
                    requireContext(),
                    Gson().toJson(billResponse.bills),
                    billResponse.reason,
                    SharedPref.getToken(requireContext())
                ).setCallback { e, result ->
                    progressDialog.dismiss()
                    when {
                        result.has("data") -> {
                            transactionData.message = "You have successfully created a Ksh. ${
                                NumberFormat.getNumberInstance(Locale.getDefault())
                                    .format(amountTotal)
                            } bill."
                            replaceFragment(SuccessFragment(transactionData, this))
                        }
                        result.has("errors") -> {
                            showToast(requireContext(), result["errors"].asJsonArray[0].asString, 0)
                        }
                        else -> {
                            showToast(requireContext(), e.message!!, 0)
                        }
                    }
                }
            }
        }
        billResponse.bills!!.forEach { contact -> arrayList.add(contact.recipient) }
        usersAdapter.setContact(arrayList)
        return view
    }

    override fun transactionComplete() {
        replaceFragment(SplitBillGroupsFragment())
    }

    override fun onSelectGroupItemForEdit(contact: MultiContactModel, pos: Int) {
        val editSplitBillBottomSheet = EditSendToManyBottomSheet(contact, pos, "Edit Split", this)
        editSplitBillBottomSheet.show(requireActivity().supportFragmentManager, "Edit Contact")
    }

    override fun onGroupItemEdited(contact: MultiContactModel, pos: Int) {
        arrayList[pos] = contact
        usersAdapter.setContact(arrayList)
        amountTotal = 0.0
        arrayList.forEach { item ->
            amountTotal += item.amount!!.toInt()
        }
        binding.tvBillTotal.text = String.format(
            "Kes %s",
            NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)
        )
    }
}