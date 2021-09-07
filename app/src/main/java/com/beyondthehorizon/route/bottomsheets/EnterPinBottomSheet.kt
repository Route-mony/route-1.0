package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.EnterPinTransactionPinBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.views.base.BaseBottomSheetFragment

class EnterPinBottomSheet(private val onInputListener: OnInputListener) :
    BaseBottomSheetFragment() {
    private var _binding: EnterPinTransactionPinBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EnterPinTransactionPinBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.dialogButtonPin.setOnClickListener {
            if (TextUtils.isEmpty(binding.enterPin.text)) {
                showToast(requireContext(), "Enter pin", 0)
                binding.enterPin.error = "Enter pin"
            }
            onInputListener.onPinListener(binding.enterPin.text.toString().trim())
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}