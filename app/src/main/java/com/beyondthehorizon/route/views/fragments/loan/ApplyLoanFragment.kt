package com.beyondthehorizon.route.views.fragments.loan

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.bottomsheets.LoanBottomSheetModel
import com.beyondthehorizon.route.databinding.FragmentApplyLoanBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment


class ApplyLoanFragment : BaseFragment(), OnInputListener, IDone {
    private var _binding: FragmentApplyLoanBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplyLoanBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.loanAmount.setText("Kes 2,000.00")
        binding.receiveAmount.setText("Kes 1,440.00")
        binding.tenor.setText("21 Days")
        binding.serviceFee.setText("Kes 560.00")
        binding.interst.setText("0.00")
        binding.dueDate.setText("")
        binding.nxtApplyLoan.setOnClickListener {
            val pr = ProgressDialog(requireContext())
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({
                pr.dismiss()
                val loanBottomModel = LoanBottomSheetModel(this)
                loanBottomModel.show(
                    (requireActivity() as AppCompatActivity).supportFragmentManager,
                    "Submit Loan"
                );
            }, 1000)
        }
        binding.arrowBack.setOnClickListener {
            replaceFragment(LoanFragment())
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onReasonListener(reason: String) {
        TODO("Not yet implemented")
    }

    override fun onPinListener(pin: String) {
        val userProfile = SharedPref.getData(
            requireContext(),
            Constants.USER_PROFILE, ProfileResponse::class.java
        ) as ProfileResponse
        transactionData.message =
            "Dear ${userProfile.getUsername()}, \nThank you for using Route.Please continue using Route services to grow your loan limit and qualify for a loan."
        replaceFragment(SuccessFragment(transactionData, this))
    }

    override fun transactionComplete() {
        replaceFragment(HomeFragment())
    }
}