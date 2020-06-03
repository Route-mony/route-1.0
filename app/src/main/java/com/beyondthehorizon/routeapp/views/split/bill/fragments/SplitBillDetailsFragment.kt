package com.beyondthehorizon.routeapp.views.split.bill.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beyondthehorizon.routeapp.R

/**
 * A simple [Fragment] subclass.
 */
class SplitBillDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_split__bill__details, container, false)
    }

}
