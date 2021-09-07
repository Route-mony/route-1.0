package com.beyondthehorizon.route.views.fragments.services.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beyondthehorizon.route.databinding.FragmentFavoriteContactsBinding
import com.beyondthehorizon.route.models.common.TransactionData

private var _binding: FragmentFavoriteContactsBinding? = null
private val binding get() = _binding!!

class FavoriteContactsFragment(transactionData: TransactionData? = null) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteContactsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
