package com.beyondthehorizon.route.views.fragments.services.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.beyondthehorizon.route.adapters.MultiChoiceContactsAdapter
import com.beyondthehorizon.route.databinding.FragmentContactsListBinding
import com.beyondthehorizon.route.models.contacts.ContactsResponse
import com.beyondthehorizon.route.utils.Constants.MY_ROUTE_CONTACTS_NEW
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment

class MultiChoiceContactsFragment : BaseFragment() {
    private var _binding: FragmentContactsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactsAdapater: MultiChoiceContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsListBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        val contacts = SharedPref.getData(
            requireContext(),
            MY_ROUTE_CONTACTS_NEW,
            ContactsResponse::class.java
        ) as ContactsResponse
        contactsAdapater = MultiChoiceContactsAdapter(requireContext())
        binding.rvContacts.adapter = contactsAdapater
        binding.contactSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                contactsAdapater.filter.filter(newText)
                return true
            }
        })
        return view
    }


}