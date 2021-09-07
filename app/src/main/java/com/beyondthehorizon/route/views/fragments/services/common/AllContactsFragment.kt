package com.beyondthehorizon.route.views.fragments.services.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.adapters.ContactsAdapter
import com.beyondthehorizon.route.databinding.FragmentContactsListBinding
import com.beyondthehorizon.route.interfaces.ISelectedContact
import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.models.contacts.ContactsResponse
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants.MY_ROUTE_CONTACTS_NEW
import com.beyondthehorizon.route.utils.Constants.USER_PROFILE
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment


class AllContactsFragment(private val data: TransactionData? = null) : BaseFragment(),
    ISelectedContact {
    private lateinit var contactsAdapter: ContactsAdapter

    private var _binding: FragmentContactsListBinding? = null
    private val binding get() = _binding!!
    private var profile: ProfileResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsListBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        transactionData = data!!
        profile = SharedPref.getData(
            requireContext(),
            USER_PROFILE,
            ProfileResponse::class.java
        ) as ProfileResponse
        contactsAdapter = ContactsAdapter(requireContext(), this)
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.adapter = contactsAdapter
        binding.contactSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                var pattern = newText.removePrefix("+").toRegex().toString()
                contactsAdapter.filter.filter(pattern)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        loadRouteContacts()
        return view
    }

    private fun loadRouteContacts() {
        val users = SharedPref.getData(
            requireActivity(),
            MY_ROUTE_CONTACTS_NEW,
            ContactsResponse::class.java
        )
        if (users != null && (users as ContactsResponse).getContactModels() != null) {
            val contacts = mutableListOf<Contact>()
            users.getContactModels()!!.forEach { contact ->
                contacts.add(
                    Contact(
                        id = contact.id!!,
                        name = contact.username!!,
                        contact = contact.phoneNumber!!,
                        avatar = contact.image!!,
                    )
                )
            }
            if (contacts.size > 0) {
                contactsAdapter.updateContacts(contacts)
            }
        }
    }

    override fun selectedContact(contact: Contact) {
        transactionData.contact = contact
        replaceFragment(AmountFragment(transactionData))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}