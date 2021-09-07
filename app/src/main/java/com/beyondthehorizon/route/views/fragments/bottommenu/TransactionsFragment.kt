package com.beyondthehorizon.route.views.fragments.bottommenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.databinding.FragmentTransactionsBinding
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.transactions.main.CashOutFragment
import com.beyondthehorizon.route.views.transactions.main.ReceivedFragment
import com.beyondthehorizon.route.views.transactions.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_transactions.*

class TransactionsFragment : BaseFragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        val sectionsPagerAdapter =
            SectionsPagerAdapter((requireActivity() as AppCompatActivity).supportFragmentManager)
        binding.tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(ReceivedFragment(), "Cash In")
        sectionsPagerAdapter.addFragment(CashOutFragment(), "Cash Out")

        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(view_pager)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}