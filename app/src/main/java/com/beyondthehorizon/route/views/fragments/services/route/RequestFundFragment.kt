package com.beyondthehorizon.route.views.fragments.services.route

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.FragmentRequestFundBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment
import com.beyondthehorizon.route.views.fragments.services.common.AmountFragment
import java.text.DecimalFormat


class RequestFundFragment(private val data: TransactionData? = null) : BaseFragment(),
    IDone {
    private var _binding: FragmentRequestFundBinding? = null
    private val binding get() = _binding!!
    private lateinit var format: DecimalFormat
    private lateinit var amount: String
    private lateinit var username: String
    private lateinit var userId: String
    private lateinit var contact: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestFundBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        amount = SharedPref.getString(requireContext(), "Amount").toString()
        username = SharedPref.getString(requireContext(), "Username").toString()
        userId = SharedPref.getString(requireContext(), "Id").toString()
        contact = SharedPref.getString(requireContext(), Constants.PHONE_NUMBER).toString()
        format = DecimalFormat("#,###")
        transactionData = data!!

        binding.txtAmount.text = format.format(amount.toInt())
        binding.txtUserContact.text = contact
        binding.txtUsername.text = username

        binding.btnRequest.setOnClickListener {
            var reason = binding.txtReason.text.toString()
            if (TextUtils.isEmpty(reason)) {
                binding.txtReason.error = "Please enter your reason"
            } else {
                Constants.requestFund(
                    requireContext(),
                    userId,
                    amount,
                    reason,
                    SharedPref.getToken(requireContext())
                ).setCallback { e, result ->
                    when {
                        e != null -> {
                            showToast(requireContext(), e.message!!, 0)
                        }
                        result.get("data") != null -> {
                            transactionData.message =
                                result.get("data").asJsonObject.get("message").asString
                            replaceFragment(SuccessFragment(transactionData, this))
                        }
                        else -> {
                            showToast(
                                requireContext(),
                                "User not registered or haven't confirmed their email",
                                0
                            );
                        }
                    }
                }
            }
        }

        binding.arrowBack.setOnClickListener {
            replaceFragment(AmountFragment(transactionData))
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun transactionComplete() {
        replaceFragment(HomeFragment())
    }
}