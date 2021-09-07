package com.beyondthehorizon.route.views.fragments.loan

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.FragmentLoanBinding
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment

class LoanFragment : BaseFragment() {
    private var _binding: FragmentLoanBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoanBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.btnApplyLoan.setOnClickListener {
            val pr = ProgressDialog(requireContext())
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({
                replaceFragment(ApplyLoanFragment())
            }, 1000)
        }
        binding.arrowBack.setOnClickListener {
            replaceFragment(HomeFragment())
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}