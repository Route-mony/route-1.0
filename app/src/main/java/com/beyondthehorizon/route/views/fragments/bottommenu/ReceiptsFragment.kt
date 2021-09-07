package com.beyondthehorizon.route.views.fragments.bottommenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.beyondthehorizon.route.adapters.ReceiptAdapter
import com.beyondthehorizon.route.bottomsheets.ReceiptDetailsBottomModel
import com.beyondthehorizon.route.databinding.FragmentReceiptsBinding
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.receipt.ui.main.ReceivedReceiptFragment
import com.beyondthehorizon.route.views.receipt.ui.main.SectionsPagerAdapter
import com.beyondthehorizon.route.views.receipt.ui.main.SentReceiptFragment
import kotlinx.android.synthetic.main.activity_receipt.*

class ReceiptsFragment : BaseFragment(), ReceiptAdapter.ReceiptInterface {
    private var _binding: FragmentReceiptsBinding? = null
    private val binding get() = _binding!!
    private lateinit var supportFragmentManager: FragmentManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceiptsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        supportFragmentManager = (requireActivity() as AppCompatActivity).supportFragmentManager
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        binding.tabs.setupWithViewPager(view_pager)
        sectionsPagerAdapter.addFragment(SentReceiptFragment(), "Sent Receipts")
        sectionsPagerAdapter.addFragment(ReceivedReceiptFragment(), "Received Receipts")

        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(view_pager)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onReceiptClicked(receipt: String, receipt_type: String) {
        val receiptDetailsBottomModel = ReceiptDetailsBottomModel()
        receiptDetailsBottomModel.show(supportFragmentManager, "Receipt")
    }
}
