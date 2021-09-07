package com.beyondthehorizon.route.views.fragments.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.FragmentSuccessBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.views.base.BaseDialogFragment


class SuccessFragment( private val data: TransactionData, private val iDone: IDone,) :
    BaseDialogFragment() {
    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.tvInfo.text = data.message
        binding.arrowBack.setOnClickListener {
            dismiss()
        }
        binding.btnDone.setOnClickListener {
            iDone.transactionComplete()
            dismiss()
        }
        return view
    }
}