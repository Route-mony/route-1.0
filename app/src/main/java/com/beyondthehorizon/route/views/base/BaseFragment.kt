package com.beyondthehorizon.route.views.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.utils.NetworkUtils
import com.beyondthehorizon.route.views.NetworkFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment

open class BaseFragment : Fragment() {
    val bundle = Bundle()
    var isNetworkAvailable: Boolean = false
    lateinit var transactionData: TransactionData

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (NetworkUtils(requireContext()).isNetworkAvailable) {
                removeFragment(NetworkFragment());
            } else {
                loadFragment(NetworkFragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNetworkAvailable = NetworkUtils(requireContext()).isNetworkAvailable
        transactionData = TransactionData()
    }

    fun startNewActivity(clazz: Class<*>, bundle: Bundle? = null, finish: Boolean? = false) {
        val intent = Intent(requireContext(), clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        if (finish!!) {
            requireActivity().finish()
        }
    }

    fun showCustomToast(context: Context, msg: String, duration: Int) {
        Toast.makeText(context, String.format(getString(R.string.unable_to_fetch), msg), duration)
            .show()
    }

    fun showToast(context: Context, msg: String, duration: Int) {
        Toast.makeText(context, msg, duration).show()
    }

    fun replaceFragment(frag: Fragment, bundle: Bundle? = null) {
        try {
            val manager = requireActivity().supportFragmentManager
            val t = manager.beginTransaction()
            if (bundle != null) {
                frag.arguments = bundle
            }
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            t.replace(R.id.frmContainer, frag).commit()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun loadFragment(fragment: Fragment) {
        try {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(android.R.id.content, fragment).addToBackStack(null).commit()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun removeFragment(fragment: Fragment) {
        val manager: FragmentManager = requireActivity().supportFragmentManager
        val trans = manager.beginTransaction()
        trans.remove(fragment)
        trans.commit()
        manager.popBackStack()
    }

    fun showCompleteDialog(transactionData: TransactionData, iDone: IDone) {
        val fragment = SuccessFragment(transactionData, iDone)
        val fragmentTransition = parentFragmentManager.beginTransaction()
        fragmentTransition.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransition.add(android.R.id.content, fragment).addToBackStack(null).commit()
    }
}