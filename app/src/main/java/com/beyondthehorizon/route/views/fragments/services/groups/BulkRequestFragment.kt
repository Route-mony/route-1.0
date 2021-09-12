package com.beyondthehorizon.route.views.fragments.services.groups

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.BulkyRequestAdapter
import com.beyondthehorizon.route.bottomsheets.AddBulkRequestItemBottomSheet
import com.beyondthehorizon.route.databinding.FragmentBulkRequestBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.AddBulkRequestItemBottomSheetListener
import com.beyondthehorizon.route.models.BulkyRequestModel
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.arrayList
import com.beyondthehorizon.route.views.arrayListJson
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.usersAdapter
import kotlinx.android.synthetic.main.activity_bulk_request.*
import kotlin.collections.emptyList

class BulkRequestFragment : BaseFragment(), AddBulkRequestItemBottomSheetListener {
    private var _binding: FragmentBulkRequestBinding? = null
    private val binding get() = _binding!!
    private val arrayList = ArrayList<BulkyRequestModel>()
    private val arrayListJson = ArrayList<String>()
    private lateinit var usersAdapter: BulkyRequestAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBulkRequestBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        usersAdapter = BulkyRequestAdapter(requireContext()) { position ->
            run {
                arrayListJson.removeAt(position - 1)
                var amountTotal = 0
                if (arrayList.size > 1) {
                    for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                        amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
                    }
                    binding.totals.text = amountTotal.toString()
                    binding.totalCard.visibility = View.VISIBLE
                    binding.emptyList.visibility = View.GONE
                } else {
                    binding.totalCard.visibility = View.GONE
                    binding.emptyList.visibility = View.VISIBLE
                }

            }
        }
        bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
        arrayList.add(BulkyRequestModel("Reason/Item", "Unit Price", "Quantity"))
        usersAdapter.setContact(arrayList)

        binding.addFab.setOnClickListener {
            val addBulkRequestItemBottomSheet = AddBulkRequestItemBottomSheet();
            addBulkRequestItemBottomSheet.show(requireActivity().supportFragmentManager, "AddBulk RequestItem");
        }
        binding.requestButton.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Constants.bulkRequest(requireContext(), SharedPref.getToken(requireContext()), "NO from designation",
                "NO from department", "NO project title", "NO to designation",
                "NO to department", prefs.getString("Id", "").toString(), arrayListJson.toString())
                .setCallback { e, result ->
                    progressDialog.dismiss()
                    if (e != null) {
                        showToast(requireContext(), e.message!!,0)
                    }
                    else{
                        transactionData.message =
                    }
                    Toast.makeText(this@BulkRequestActivity, "Request send successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@BulkRequestActivity, FundRequestedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("Message", "Request send successfully")
                    startActivity(intent)
                }
        }

        back.setOnClickListener {
            onBackPressed()
        }
        return view
    }

    override fun addNewBulkRequestItem(arrayList2: ArrayList<BulkyRequestModel>, arrayListJson2: ArrayList<String>) {
        var amountTotal = 0
        arrayListJson.addAll(arrayListJson2)
        arrayList.addAll(arrayList2)
        if (arrayList.size > 1) {
//                    val newArray = Arrays.copyOfRange(arrayList, 1, arrayList.size - 1);
            for (bulkyRequestModel: BulkyRequestModel in arrayList.subList(1, arrayList.size)) {
                amountTotal += bulkyRequestModel.amount.toInt() * bulkyRequestModel.quantity.toInt()
//                Log.e("NAHAPA", amountTotal.toString())
            }
            usersAdapter.setContact(arrayList)
            totals.text = amountTotal.toString()
            totalCard.visibility = View.VISIBLE
            emptyList.visibility = View.GONE
        }
    }

    override fun onResume() {
        arrayList.clear()
        arrayListJson.clear()
        arrayList.add(BulkyRequestModel("Reason/Item", "Unit Price", "Quantity"))
        usersAdapter.setContact(arrayList)
        super.onResume()
    }

    override fun onAddBulkRequestListener(bulkyRequestModel: BulkyRequestModel) {
        TODO("Not yet implemented")
    }
}