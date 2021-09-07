package com.beyondthehorizon.route.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.databinding.FragmentNetworkBinding
import com.beyondthehorizon.route.views.base.BaseFragment

private var _binding: FragmentNetworkBinding? = null
private val binding get() = _binding!!

class NetworkFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNetworkBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}