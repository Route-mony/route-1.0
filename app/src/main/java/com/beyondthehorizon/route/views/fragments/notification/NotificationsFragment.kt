package com.beyondthehorizon.route.views.fragments.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.adapters.RequestFundSectionsPagerAdapter
import com.beyondthehorizon.route.databinding.FragmentNotificationsBinding
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment

class NotificationsFragment : BaseFragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        val sectionsPagerAdapter =
            RequestFundSectionsPagerAdapter((requireActivity() as AppCompatActivity).supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.viewPager)
        sectionsPagerAdapter.addFragment(SentNotificationFragment(), "Sent")
        sectionsPagerAdapter.addFragment(ReceivedNotificationFragment(), "Received")
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
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