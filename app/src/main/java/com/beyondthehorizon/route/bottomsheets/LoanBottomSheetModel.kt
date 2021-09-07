package com.beyondthehorizon.route.bottomsheets

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.LoanRequestOptionsBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref.Companion.getData
import com.beyondthehorizon.route.views.base.BaseBottomSheetFragment

class LoanBottomSheetModel(private val onInputListener: OnInputListener) :
    BaseBottomSheetFragment() {
    private var _binding: LoanRequestOptionsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoanRequestOptionsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        val profileResponse = getData(
            requireContext(),
            Constants.USER_PROFILE,
            ProfileResponse::class.java
        ) as ProfileResponse?
        binding.userName.text = profileResponse!!.getFullName()
        binding.nxtApplyLoan.setOnClickListener { view: View? ->
            val pr = ProgressDialog(requireActivity())
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({

                // yourMethod();
                pr.dismiss()
                onInputListener.onPinListener("")
            }, 1000)
        }
        return view
    }
}