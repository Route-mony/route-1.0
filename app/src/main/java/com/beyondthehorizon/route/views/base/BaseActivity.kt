package com.beyondthehorizon.route.views.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.utils.Constants.MY_ROUTE_CONTACTS_NEW
import com.beyondthehorizon.route.utils.NetworkUtils
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.NetworkFragment


open class BaseActivity : AppCompatActivity() {
    val bundle = Bundle()
    var transactionData: TransactionData = TransactionData()

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPref.remove(this, MY_ROUTE_CONTACTS_NEW)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (NetworkUtils(this@BaseActivity).isNetworkAvailable) {
                removeFragment(NetworkFragment());
            } else {
                loadFragment(NetworkFragment())
            }
        }
    }

    fun startNewActivity(clazz: Class<*>, bundle: Bundle? = null, finish: Boolean? = false) {
        val intent = Intent(this, clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        if (finish!!) {
            finish()
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
            val manager = this.supportFragmentManager
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
            val fragmentManager = this.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(android.R.id.content, fragment).addToBackStack(null).commit()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun removeFragment(fragment: Fragment) {
        val manager: FragmentManager = this.supportFragmentManager
        val trans = manager.beginTransaction()
        trans.remove(fragment)
        trans.commit()
        manager.popBackStack()
    }
}