package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.CustomSendMoneyAlertDialogLayoutBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.SendMoneyBottomSheetListener
import com.beyondthehorizon.route.models.SendMoneyModel
import com.beyondthehorizon.route.models.providers.ProviderResponse
import com.beyondthehorizon.route.utils.Constants.SEND_MONEY
import com.beyondthehorizon.route.utils.Constants.WALLET_PROVIDERS
import com.beyondthehorizon.route.utils.SharedPref.Companion.getData
import com.beyondthehorizon.route.utils.Utils
import com.beyondthehorizon.route.views.base.BaseBottomSheetFragment
import com.beyondthehorizon.route.views.fragments.services.common.AmountFragment
import com.beyondthehorizon.route.views.fragments.services.common.ContactsFragment
import java.util.*

class SendMoneyBottomModel(private val sendMoneyListener: SendMoneyBottomSheetListener) :
    BaseBottomSheetFragment() {
    private var _binding: CustomSendMoneyAlertDialogLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomSendMoneyAlertDialogLayoutBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.toRoute.setOnClickListener {
            transactionData.process = SEND_MONEY
            replaceFragment(ContactsFragment(transactionData))
            dismiss()
        }
        binding.toMobileMoney.setOnClickListener {
            binding.sendLayout.visibility = View.GONE
            binding.bankLayout.visibility = View.GONE
            binding.mobileLayout.visibility = View.VISIBLE
        }
        binding.toBank.setOnClickListener {
            binding.sendLayout.visibility = View.GONE
            binding.mobileLayout.visibility = View.GONE
            binding.bankLayout.visibility = View.VISIBLE

        }

        binding.imgSearch.setOnClickListener {
            transactionData.fetchAllContacts = true
            transactionData.process = SEND_MONEY
            replaceFragment(ContactsFragment(transactionData))
            dismiss()
        }

        binding.mobileButton.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.mobileNumber.text) -> {
                    binding.mobileNumber.error = "Enter mobile number"
                    showToast(requireContext(), "Enter mobile number", 0)
                }
                Utils.isPhoneNumberValid(binding.mobileNumber.text.toString(), "KE") -> {
                    showToast(requireContext(), "Enter a valid mobile number", 0)
                    binding.mobileNumber.error = "Enter a valid mobile number"
                }
                else -> {
                    transactionData.phone = binding.mobileNumber.text.toString().trim()
                    transactionData.process = SEND_MONEY
                    replaceFragment(AmountFragment(transactionData))
                    dismiss()
                }
            }
        }
        /**
         * LOAD BANK PROVIDERS
         */
        try {
            val providers = getData(
                requireContext(),
                WALLET_PROVIDERS,
                ProviderResponse::class.java
            ) as ProviderResponse
            val banks = providers.getBankProviders()!!.map { it.providerName }
            val banksAdapter = ArrayAdapter(
                requireContext(),
                R.layout.custom_list_item,
                R.id.text_view_list_item,
                banks
            )
            banksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.findBank.setAdapter(banksAdapter)
            binding.bankButton.setOnClickListener {

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        binding.bankButton.setOnClickListener {
            if (TextUtils.isEmpty(binding.accountNumber.text)) {
                binding.accountNumber.error = "Enter account number"
                showToast(requireContext(), "Enter account number", 0)
                binding.accountNumber.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.findBank.text)) {
                showToast(requireContext(), "Select bank", 0)
                binding.findBank.error = "Select bank"
                binding.findBank.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.amount.text)) {
                showToast(requireContext(), "Enter amount", 0)
                binding.amount.error = "Enter amount"
                binding.amount.requestFocus()
                return@setOnClickListener
            }
            val sendMoneyModel = SendMoneyModel()
            sendMoneyModel.isBank = true
            sendMoneyModel.bankName = binding.findBank.text.toString().trim()
            sendMoneyModel.bankAccountNo = binding.accountNumber.text.toString().trim()
            sendMoneyListener.moneyModelListener(sendMoneyModel)
            dismiss()
        }
        return view
    }
}