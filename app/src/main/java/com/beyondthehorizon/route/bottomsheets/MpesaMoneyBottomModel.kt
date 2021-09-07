package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.MpesaPaymentOptionsBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.SendMoneyBottomSheetListener
import com.beyondthehorizon.route.models.SendMoneyModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.mpesa_payment_options.*

class MpesaMoneyBottomModel(private val sendMoneyListener: SendMoneyBottomSheetListener) :
    BottomSheetDialogFragment() {
    private var _binding: MpesaPaymentOptionsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MpesaPaymentOptionsBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.buyGoods.setOnClickListener {
            binding.parentLayout.visibility = View.GONE
            binding.payBillLayout.visibility = View.GONE
            binding.buyGoodsLayout.visibility = View.VISIBLE
        }
        binding.payBill.setOnClickListener {
            binding.parentLayout.visibility = View.GONE
            binding.buyGoodsLayout.visibility = View.GONE
            binding.payBillLayout.visibility = View.VISIBLE
        }
        binding.buyGoodsButton.setOnClickListener {
            if (TextUtils.isEmpty(binding.tillNumber.text)) {
                binding.tillNumber.error = "Enter till number"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.tillAmount.text)) {
                binding.tillAmount.error = "Enter amount"
                return@setOnClickListener
            }
            sendMoneyListener.moneyModelListener(
                SendMoneyModel(
                    isMobile = true,
                    bankAccountNo = binding.tillNumber.text.toString(),
                    amount = binding.tillAmount.text.toString().toInt()
                )
            )
            dismiss()
        }

        binding.payBillButton.setOnClickListener {
            if (TextUtils.isEmpty(binding.businessNumber.text)) {
                binding.businessNumber.error = "Enter business number"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.businessAccount.text)) {
                binding.businessNumber.error = "Enter business account"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.businessAmount.text)) {
                binding.businessNumber.error = "Enter amount"
                return@setOnClickListener
            }
            sendMoneyListener.moneyModelListener(
                SendMoneyModel(
                    amount = binding.businessAmount.text.toString().toInt(),
                    bankName = binding.businessNumber.text.toString(),
                    bankAccountNo = binding.businessAccount.text.toString()
                )
            )
            dismiss()
        }
        return view
    }
}