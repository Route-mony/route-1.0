package com.beyondthehorizon.route.views

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.*
import com.beyondthehorizon.route.databinding.ActivityMainBinding
import com.beyondthehorizon.route.models.contacts.ContactsResponse
import com.beyondthehorizon.route.models.contacts.MultiContactModel
import com.beyondthehorizon.route.models.contacts.MyContactsViewModel
import com.beyondthehorizon.route.models.contacts.MyViewModelFactory
import com.beyondthehorizon.route.models.providers.ProviderResponse
import com.beyondthehorizon.route.utils.*
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.viewmodels.RoutViewModel
import com.beyondthehorizon.route.views.base.BaseActivity
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.ReceiptsFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.SettingsFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.TransactionsFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.coroutines.suspendCoroutine

class MainActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {
    private var networkUtils: NetworkUtils? = null
    private var moveUp: Animation? = null
    private var util: Utils? = null
    private var routViewModel: RoutViewModel? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MyContactsViewModel
    private val REQUEST_READ_CONTACTS = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myViewModelFactory = MyViewModelFactory(application, this)
        viewModel = ViewModelProvider(this, myViewModelFactory).get(MyContactsViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
        util = Utils(this)
        networkUtils = NetworkUtils(this)
        moveUp = AnimationUtils.loadAnimation(applicationContext, R.anim.move_up)
        routViewModel = ViewModelProviders.of(this).get(RoutViewModel::class.java)
        binding.bottomNav.setOnItemSelectedListener(this)
        binding.btnRetry.setOnClickListener {
            loadingContacts(getString(R.string.please_wait), true)
            checkPermissions()
        }
        loadingContacts(getString(R.string.please_wait), true)
        checkPermissions()
    }

    private fun loadingContacts(msg: String, loading: Boolean, success: Boolean? = false) {
        if (success!!) {
            binding.llLoadingContacts.visibility = View.GONE
            binding.bottomNav.visibility = View.VISIBLE
            return
        }
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.ivIcon.visibility = View.GONE
            binding.btnRetry.visibility = View.GONE
            binding.tvLoading.text = msg
        } else {
            binding.progressBar.visibility = View.GONE
            binding.ivIcon.visibility = View.VISIBLE
            binding.btnRetry.visibility = View.VISIBLE
            binding.tvLoading.text = String.format(getString(R.string.unable_to_fetch), msg)
        }
    }

    private fun checkPermissions() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val builder = AlertDialog.Builder(this@MainActivity, R.style.MyDialogTheme)
                    .setTitle("Route Would Like to Access Your Contacts")
                    .setMessage(R.string.permisson_txt)
                    .setCancelable(true)
                    .setPositiveButton(
                        "ALLOW"
                    ) { _: DialogInterface, _: Int ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            REQUEST_READ_CONTACTS
                        )
                    }
                builder.setNegativeButton("DENY") { dialog, id ->
                    dialog.dismiss()
                }.create()
                builder.show()
            } else {
                viewModel.myContacts.observe(this, {
                    loadDataInBackground(it!!)
                })
            }
        } catch (ex: Exception) {
            Timber.d(ex)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.myContacts.observe(this, {
                    loadDataInBackground(it!!)
                })
            }
        }
    }

    private suspend fun fetchRouteContacts(myContactsList: List<MultiContactModel>): Unit =
        suspendCoroutine {
            val json = Gson().toJson(myContactsList)
            getRegisteredRouteContacts(
                this@MainActivity,
                SharedPref.getToken(this@MainActivity),
                json
            )
                .setCallback { e, result ->
                    runOnUiThread {
                        if (e != null) {
                            showToast(this, e.message!!, 0)
                            loadingContacts("contacts", loading = false, success = false)
                            return@runOnUiThread
                        }
                        if (result != null && result.has("data")) {
                            val contacts = Gson().fromJson(result, ContactsResponse::class.java)
                            SharedPref.save(this@MainActivity, MY_ROUTE_CONTACTS_NEW, contacts)
                            loadingContacts("contacts", loading = false, success = true)
                        } else {
                            if (result.has("detail")) {
                                loadingContacts(
                                    result.get("detail").asString,
                                    loading = false,
                                    success = false
                                )
                            } else {
                                loadingContacts("contacts", loading = false, success = false)
                            }
                        }
                    }
                }
        }

    private fun loadDataInBackground(myContactsList: List<MultiContactModel>) {
        GlobalScope.launch {
            awaitAll(
                async { fetchRouteContacts(myContactsList) },
                async { getServiceProviders() },
                async { sendRegistrationToServer() })
        }
    }


    private suspend fun sendRegistrationToServer(): Unit = suspendCoroutine {
        FirebaseInstallations.getInstance().getToken(true)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (task.exception != null) {
                        task.exception!!.printStackTrace()
                    }
                    return@OnCompleteListener
                }
                updateFirebaseToken(
                    this@MainActivity,
                    SharedPref.getToken(this@MainActivity),
                    task.result!!.token
                )
                    .setCallback { e: Exception?, _: JsonObject? -> e?.printStackTrace() }
            })
    }

    private suspend fun getServiceProviders(): Unit? = suspendCoroutine {
        getServiceProviders(this@MainActivity, SharedPref.getToken(this@MainActivity))
            .setCallback { e: Exception?, result: JsonObject ->
                runOnUiThread {
                    when {
                        e != null -> {
                            e.printStackTrace()
                            showToast(this, e.message!!, 0)
                        }
                        result.has("data") -> {
                            val providers = Gson().fromJson(result, ProviderResponse::class.java)
                            SharedPref.save(this, WALLET_PROVIDERS, providers)
                        }
                        else -> {
                            showCustomToast(this, "service providers", 0)
                        }
                    }
                }
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemHome -> {
                replaceFragment(HomeFragment())
                return true
            }
            R.id.menuItemTransaction -> {
                replaceFragment(TransactionsFragment())
                return true
            }
            R.id.menuItemReceipts -> {
                replaceFragment(ReceiptsFragment())
                return true
            }
            R.id.menuItemSettings -> {
                replaceFragment(SettingsFragment())
                return true
            }
        }
        return false
    }
}
