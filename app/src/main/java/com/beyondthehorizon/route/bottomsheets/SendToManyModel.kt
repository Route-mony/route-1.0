package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.CustomSendToManyAlertDialogLayoutBinding
import com.beyondthehorizon.route.utils.Constants.SEND_TO_MANY
import com.beyondthehorizon.route.views.base.BaseBottomSheetFragment
import com.beyondthehorizon.route.views.fragments.services.groups.send.SendToManyGroupsFragment

class SendToManyModel : BaseBottomSheetFragment() {
    private var _binding: CustomSendToManyAlertDialogLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            CustomSendToManyAlertDialogLayoutBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.toRoute.setOnClickListener {
            transactionData.process = SEND_TO_MANY
            replaceFragment(SendToManyGroupsFragment(transactionData))
        }
        binding.toMobileMoney.setOnClickListener {
            transactionData.process = SEND_TO_MANY
            transactionData.fetchAllContacts = true
            replaceFragment(SendToManyGroupsFragment(transactionData))
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}