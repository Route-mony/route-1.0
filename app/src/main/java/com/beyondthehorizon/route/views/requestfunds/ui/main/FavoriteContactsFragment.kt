package com.beyondthehorizon.route.views.requestfunds.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beyondthehorizon.route.R

/**
 * A simple [Fragment] subclass.
 */
class FavoriteContactsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_contacts, container, false)
    }
}
