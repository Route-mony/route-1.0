package com.beyondthehorizon.route.views.fragments.services.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.adapters.RequestFundSectionsPagerAdapter
import com.beyondthehorizon.route.databinding.FragmentContactsBinding
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment

class ContactsFragment(private val data: TransactionData? = null) : BaseFragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        val sectionsPagerAdapter =
            RequestFundSectionsPagerAdapter((requireActivity() as AppCompatActivity).supportFragmentManager)
        sectionsPagerAdapter.addFragment(AllContactsFragment(data), "Phone Number")
        sectionsPagerAdapter.addFragment(FavoriteContactsFragment(data), "Favorites")
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.arrowBack.setOnClickListener { replaceFragment(HomeFragment()) }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}