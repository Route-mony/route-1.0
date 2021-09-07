package com.beyondthehorizon.route.views.fragments.services.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.FragmentMultiContactsBinding
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.utils.Constants.SEND_MONEY
import com.beyondthehorizon.route.utils.Constants.SPLIT_BILL
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.services.groups.send.SendToManyFragment

class MultiContactsFragment(private val data: TransactionData? = null) : BaseFragment() {
    private var _binding: FragmentMultiContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMultiContactsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        transactionData = data!!
        binding.chooseContacts.setOnClickListener {
            when (transactionData.process) {
                SEND_MONEY -> {
                    replaceFragment(SendToManyFragment())

                }
                SPLIT_BILL -> {
                    replaceFragment(SendToManyFragment())
                }
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}