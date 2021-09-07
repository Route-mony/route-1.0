package com.beyondthehorizon.route.views.fragments.services.groups.send

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.GroupSendToManyAdapter
import com.beyondthehorizon.route.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.route.bottomsheets.EnterGroupBottomSheet
import com.beyondthehorizon.route.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.route.databinding.FragmentSendToManyBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.interfaces.bottomsheets.EditSendToManyBottomSheetListener
import com.beyondthehorizon.route.interfaces.bottomsheets.OnGroupEntryListener
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.models.SavedGroupItem
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.models.contacts.MultiContactModel
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment
import com.google.gson.Gson
import java.text.NumberFormat
import java.util.*

class SendToManyFragment(
    private val data: TransactionData? = null,
    private val group: SavedGroupItem? = null
) :
    BaseFragment(), EditSendToManyBottomSheetListener,
    OnInputListener, OnGroupEntryListener, IDone {
    private var _binding: FragmentSendToManyBinding? = null
    private val binding get() = _binding!!
    private var contacts = mutableListOf<MultiContactModel>()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var usersAdapter: GroupSendToManyAdapter
    private lateinit var messagetxt: String
    private lateinit var selectedGroup: SavedGroupItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendToManyBinding.inflate(layoutInflater, container, false)
        binding.tvGroupName.text = group!!.group_name
        val view = binding.root
        transactionData = data!!
        selectedGroup = group
        binding.submitButton.setOnClickListener {
            val enterPinBottomSheet = EnterPinBottomSheet(this)
            enterPinBottomSheet.show(requireActivity().supportFragmentManager, "Enter Pin")
        }

        usersAdapter = GroupSendToManyAdapter(requireContext(), this) { run {} }
        binding.bulkRequestRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }

        binding.back.setOnClickListener {
            replaceFragment(SendToManyGroupsFragment(transactionData))
        }
        contacts = selectedGroup.recipients.contacts
        setGroupContacts(contacts)
        return view
    }

    private fun setGroupContacts(contactsModel: MutableList<MultiContactModel>) {
        usersAdapter.setContact(contactsModel)
        var amountTotal = 0
        contactsModel.forEach { contact ->
            amountTotal += contact.amount!!.toInt()
        }
        binding.totalAmount.text = String.format(
            "Kes %s",
            NumberFormat.getNumberInstance(Locale.getDefault()).format(amountTotal)
        )

    }

    override fun onSelectGroupItemForEdit(item: MultiContactModel, itemPos: Int) {
        val editSendToManyBottomSheet =
            EditSendToManyBottomSheet(item, itemPos, getString(R.string.edit_beneficiary), this)
        editSendToManyBottomSheet.show(requireActivity().supportFragmentManager, "Edit Contact")
    }

    override fun onGroupItemEdited(updatedItem: MultiContactModel, position: Int) {
        contacts[position] = updatedItem
        setGroupContacts(contacts)
    }

    override fun onReasonListener(reason: String) {
        TODO("Not yet implemented")
    }

    override fun onPinListener(pin: String) {
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        contacts = contacts.filter { it.amount!!.toInt() > 0 }.toMutableList()

        if (contacts.isNotEmpty()) {
            Constants.sendToMany(
                requireContext(),
                pin,
                transactionData.provider,
                "",
                SharedPref.getToken(requireContext()),
                Gson().toJson(contacts)
            )
                .setCallback { e, result ->
                    progressDialog.dismiss()
                    if (result.has("errors")) {
                        showToast(
                            requireContext(),
                            result["errors"].asJsonArray[0].asString,
                            0
                        )
                    } else
                        if (result["status"].toString().contains("success")) {
                            if (group!!.group_name.isNotEmpty()) {
                                val enterGroupBottomSheet = EnterGroupBottomSheet(this)
                                enterGroupBottomSheet.show(
                                    requireActivity().supportFragmentManager,
                                    "Save Group"
                                )
                            } else {
                                transactionData.message =
                                    result["data"].asJsonObject.get("message").asString
                                replaceFragment(SuccessFragment(transactionData, this))
                            }
                        }
                }
        } else {
            progressDialog.dismiss()
            showToast(
                requireContext(),
                "Please enter valid amounts,\n amount cannot be zero or negative",
                0
            )
        }
    }

    override fun onCaptureGroupName(name: String) {
        Constants.saveSendToManyGroup(
            requireContext(),
            SharedPref.getToken(requireContext()),
            Gson().toJson(contacts),
            name
        ).setCallback { e, result ->
            if (result["status"].toString().contains("success")) {
                transactionData.message = messagetxt
                replaceFragment(SuccessFragment(transactionData, this))
            } else {
                showToast(requireContext(), "Amount cannot be zero or negative", 0)
            }
        }
    }

    override fun transactionComplete() {
        replaceFragment(HomeFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
