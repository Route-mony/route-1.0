package com.beyondthehorizon.routeapp.views.transactions.main

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.TransactionsAdapter
import com.beyondthehorizon.routeapp.models.TransactionModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_sent.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class SentFragment : Fragment() {

    private lateinit var transactionsAdapter: TransactionsAdapter
    private lateinit var prefs: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sent, container, false)

        prefs = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        transactionsAdapter = TransactionsAdapter(activity!!)
//        loadSentTransactions()


        return view
    }

//    private fun loadSentTransactions()
}
