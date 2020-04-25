package com.beyondthehorizon.routeapp.views.requestfunds.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.getIntent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.ContactsAdapater
import com.beyondthehorizon.routeapp.adapters.RecyclerItemClickListener
import com.beyondthehorizon.routeapp.adapters.RecyclerItemClickListener.OnItemClickListener
import com.beyondthehorizon.routeapp.databinding.FragmentRequestFundsBinding
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.views.ConfirmFundRequestActivity
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.interswitchgroup.mobpaylib.MobPay
import com.interswitchgroup.mobpaylib.model.*
import kotlinx.android.synthetic.main.enter_pin_transaction_pin.view.*
import java.lang.Exception
import java.security.SecureRandom


/**
 * A simple [Fragment] subclass.
 */
class RequestFundsFragment : Fragment() {
    private var contacts: MutableList<Contact> = mutableListOf()
    private var contactMap: MutableMap<String, Contact> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var searchView: SearchView
    private lateinit var parentIntent: Intent
    private lateinit var cardStatus: String
    private lateinit var childIntent: Intent
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: FragmentRequestFundsBinding
    private lateinit var transactionType: String
    private lateinit var contactsAdapater: ContactsAdapater
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var dummyId = 0
    private lateinit var mobileNumber: String
    private var REQUEST_READ_CONTACTS = 79
    private lateinit var token: String
    //    private lateinit var context: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for activity fragment
//        binding = DataBindingUtil.inflate(R.layout.fragment_request_funds, container, false)

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_request_funds, container, false)

        val view = binding.root
        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        searchView = binding.contactSearchView
        contacts = mutableListOf()
        contactMap = mutableMapOf()
        contactsAdapater = ContactsAdapater(activity!!, contacts)

        prefs = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        parentIntent = activity!!.intent
        childIntent = Intent(activity!!, ConfirmFundRequestActivity::class.java)
        val progressBar = CustomProgressBar()
        var transactionMessage = ""
        token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")

        transactionType = prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "").toString()

        try {
            if (ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                loadRouteContacts()
            } else {
                requestPermission();
            }

        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                var pattern = newText.toLowerCase().toRegex()
                try {
                    var filteredContacts = contacts.filter { pattern.containsMatchIn(it.contact) || pattern.containsMatchIn(it.name.toLowerCase()) }
                    var adapter = ContactsAdapater(activity!!, filteredContacts.toMutableList())
                    recyclerView.layoutManager = linearLayoutManager
                    recyclerView.setHasFixedSize(true)
                    contactsAdapater = ContactsAdapater(activity!!, filteredContacts.toMutableList())
                    recyclerView.adapter = adapter
                } catch (ex: Exception) {
                    Toast.makeText(activity!!, ex.message, Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        binding.narrationTextInputLayout.visibility = View.GONE
        try {
            if (transactionType.compareTo(Constants.REQUEST_MONEY) == 0) {
//                username = prefs.getString("Username", "").toString()
//                binding.requestTitle.text = "Request $username"
                binding.narrationTextInputLayout.visibility = View.VISIBLE
            } else if (transactionType.compareTo(Constants.SEND_MONEY) == 0) {
                if (prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(Constants.SEND_MONEY_TO_MOBILE_MONEY) == 0) {
//                    phone = parentIntent.getStringExtra(Constants.PHONE_NUMBER)
                    binding.btnRequest.text = "SEND"
//                    binding.requestTitle.text = "Send To ${phone}"

                } else if (prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(Constants.SEND_MONEY_TO_ROUTE) == 0) {
//                    username = prefs.getString("Username", "").toString()
                    binding.btnRequest.text = "SEND"
//                    binding.requestTitle.text = "Send To ${username}"

                } else if (prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(Constants.SEND_MONEY_TO_BANK) == 0) {
//                    username = prefs.getString("chosenBank", "").toString() + "\nAccount No: " + prefs.getString("bankAcNumber", "").toString()
                    binding.btnRequest.text = "SEND"
//                    binding.requestTitle.text = "Send To ${username}"

                }
            } else if (transactionType.compareTo(Constants.LOAD_WALLET_FROM_CARD) == 0 || transactionType.compareTo(Constants.LOAD_WALLET_FROM_MPESA) == 0) {
                binding.btnRequest.text = "PAY"
//                binding.requestTitle.text = "Enter Amount to Pay"
            } else if (transactionType.compareTo(Constants.MOBILE_TRANSACTION) == 0) {
//                phone = parentIntent.getStringExtra(Constants.PHONE_NUMBER)
//                binding.requestTitle.text = "Pay From ${phone}"
                binding.btnRequest.text = "PAY"
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.message, Toast.LENGTH_LONG)
        }

//        binding.contactRecyclerView.addOnItemTouchListener(new )
//        binding.contactRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(getActivity(), binding.contactRecyclerView,
//                RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                // ...
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                // ...
//            }
//        }));
        binding.contactRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(activity, binding.contactRecyclerView, object :
                OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                //do your work here..
//                Log.e("RequestFundsFragment", "object ${contacts.get(position).contact}")
                binding.contact.setText(contacts.get(position).contact)
                mobileNumber = binding.contact.text.toString()
            }

            override fun onItemLongClick(view: View?, position: Int) {
                // TODO("do nothing")
            }
        }))


        binding.btnRequest.setOnClickListener {
            val lower = 100000000
            val upper = 999999999
            val secureRandom = SecureRandom()
            var merchantId = "ROUTEK0001"
            var domain = "ISWKE"
            var transactionRef = ((Math.random() * (upper - lower)).toInt() + lower).toString()
            var terminalId = "3TLP0001"
            var currency = "KES"
            var orderId = "ROUTE_TZD_${secureRandom.nextInt(10000)}"
            var preauth = "1"
            var customerId = prefs.getString(Constants.USER_ID, "")
            var customerEmail = prefs.getString(Constants.USER_EMAIL, "")
            var clientId = "IKIAF9CED95CD2EA93B367E5E1B580A1EDB06F9EEF6D"
            var clientSecret = "g9n6CRhxzmADCz5H9IaxtmfFxfFh+jGVFCVae4+1Kko="

            try {

                var amount = binding.txtAmount.text.toString()

                if (binding.contact.text.isNullOrEmpty()) {
                    Toast.makeText(activity, "Please enter contact", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (binding.txtAmount.text.isNullOrEmpty()) {
                    Toast.makeText(activity, "Please enter amount to request", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else if (amount.toInt() <= 0) {
                    Toast.makeText(activity, "Please enter a valid amount", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (binding.narrationTextInputLayout.visibility == View.VISIBLE) {

                    if (binding.narration.text.isNullOrEmpty()) {
                        Toast.makeText(activity, "Please enter narration", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }

                var merchant = Merchant(merchantId, domain);
                var payment = Payment(amount, transactionRef, "MOBILE", terminalId, "CRD", currency, orderId)
                payment.setPreauth(preauth)
                var customer = Customer(customerId)
                customer.setEmail(customerEmail)


                /**HERE NOW*/


                /**HERE NOW*/
                if (transactionType.compareTo(Constants.REQUEST_MONEY) == 0) {
                    editor.putString("Amount", amount)
                    editor.apply()
                    startActivity(childIntent)
                } else if (transactionType.compareTo(Constants.SEND_MONEY) == 0) {
                    showSendMoneyDialog()
                } else if (transactionType.compareTo(Constants.LOAD_WALLET_FROM_CARD) == 0) {
                    var country = parentIntent.getStringExtra(Constants.COUNTRY)
                    var cardNumber = parentIntent.getStringExtra(Constants.CARD_NUMBER)
                    var expDate = parentIntent.getStringExtra(Constants.EXPIRY_DATE)
                    var expYear = expDate.substring(3, 5)
                    var expMonth = expDate.substring(0, 2)
                    var cvvNumber = parentIntent.getStringExtra(Constants.CVV_NUMBER)
                    cardStatus = parentIntent.getStringExtra(Constants.CARD_STATUS)
                    var card = Card(cardNumber, cvvNumber, expYear, expMonth);
                    lateinit var mobPay: MobPay;

                    var config = MobPay.Config();
                    mobPay = MobPay.getInstance(activity, clientId, clientSecret, config)

                    progressBar.show(activity!!, "Processing payment...")
                    mobPay.makeCardPayment(
                            card,
                            merchant,
                            payment,
                            customer, {
                        progressBar.dialog.dismiss()
                        Log.d("INTERSWITCH_MESSAGE", it.transactionOrderId)

                        if (cardStatus.compareTo(Constants.NEW_CARD) == 0) {
                            progressBar.show(activity!!, "Updating route ...")
                            Constants.addPaymentCard(activity, cardNumber, expDate, cvvNumber, country, token)
                                    .setCallback { e, result ->
                                        progressBar.dialog.dismiss()
                                        try {
                                            if (result != null) {
                                                if (result.has("errors")) {
                                                    var error = result.get("errors").asJsonObject.get("card_number").asJsonArray.get(0).asString
                                                    Toast.makeText(activity!!, error, Toast.LENGTH_LONG).show()
                                                } else {
                                                    transactionMessage = result.get("data").asJsonObject.get("message").asString
                                                    Toast.makeText(activity!!, transactionMessage, Toast.LENGTH_LONG).show()
                                                }

                                            } else if (e != null) {
                                                Log.d("INTERSWITCH_MESSAGE", e.toString())
                                                Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
                                            }
                                        } catch (ex: Exception) {
                                            Log.d("INTERSWITCH_MESSAGE", ex.message)
                                        }
                                    }
                        }
                        transactionMessage = "Ksh. ${amount} was successfully loaded to your route wallet  from card number ${cardNumber}. Transaction reference no:\t${it.transactionOrderId}"
                        val intent = Intent(activity, FundRequestedActivity::class.java)
                        editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                        editor.apply()
                        intent.putExtra("Message", transactionMessage)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra(Constants.ACTIVITY_TYPE, Constants.ADD_MONEY_ACTIVITY)
                        startActivity(intent)

                    }, {
                        progressBar.dialog.dismiss()
                        Log.d("INTERSWITCH_MESSAGE", it.message)
                        Toast.makeText(activity!!, it.message, Toast.LENGTH_LONG).show()
                    });

                } else if (transactionType.compareTo(Constants.MOBILE_TRANSACTION) == 0) {
                    try {
//                        var mobileNumber = parentIntent.getStringExtra(Constants.PHONE_NUMBER)
                        var mobile = Mobile(mobileNumber, Mobile.Type.MPESA)
                        var mobPay: MobPay
                        mobPay = MobPay.getInstance(activity!!, clientId, clientSecret, null)
                        progressBar.show(activity!!, "Processing payment...")
                        mobPay.makeMobileMoneyPayment(
                                mobile,
                                merchant,
                                payment,
                                customer, {
                            progressBar.dialog.dismiss()
                            editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                            editor.apply()
                            transactionMessage = "Ksh. ${amount} was successfully loaded to your route wallet  from mobile number ${mobileNumber}. Transaction reference no:\t${it.transactionOrderId}"
                            val intent = Intent(activity, FundRequestedActivity::class.java)
                            editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                            editor.apply()
                            intent.putExtra("Message", transactionMessage)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra(Constants.ACTIVITY_TYPE, Constants.ADD_MONEY_ACTIVITY)
                            startActivity(intent)
                        }, {
                            progressBar.dialog.dismiss()
                            Log.d("INTERSWITCH_MESSAGE", it.toString())
                            Toast.makeText(activity!!, it.message, Toast.LENGTH_LONG).show()

                        })
                    } catch (e: Exception) {
                        Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(activity, ex.message, Toast.LENGTH_LONG)
            }
        }
        return view
    }


    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()

                } else {
                    loadRouteContacts()
                }
            }
        }
    }

    private fun loadPhoneContacts() {
        try {
            val phones = activity!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            while (phones!!.moveToNext()) {
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                var cleanedPhoneNumber = phoneNumber.replace("-", "").replace(" ", "").replaceBefore("7", "0")
                var id = phoneNumber.hashCode().toString()
                contactMap.put(cleanedPhoneNumber, Contact(id, name, phoneNumber))
            }
        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadRouteContacts() {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            Constants.loadUserContacts(activity!!, token).setCallback { e, result ->
                if (result != null) {
                    mapContactsToList(result.getAsJsonArray("rows"))
                }
            }
        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun mapContactsToList(result: JsonArray) {
        loadPhoneContacts()

        if (result != null) {
            for (item: JsonElement in result) {
                var phone = item.asJsonObject.get("phone_number").asString.replace("-", "").replace(" ", "").replaceBefore("7", "0")
                var accountNumber = item.asJsonObject.get("wallet_account").asJsonObject.get("wallet_account").toString()

//                Log.e("RequestFundsActivity", item.asJsonObject.get("wallet_account").asJsonObject.get("wallet_account").toString())

                if (contactMap.keys.contains(phone)) {
                    var id = item.asJsonObject.get("id").asString
                    var avatar = R.drawable.group416
                    contactMap.getValue(phone).id = id
                    contactMap.getValue(phone).contact = item.asJsonObject.get("phone_number").asString
                    contactMap.getValue(phone).avatar = avatar
                    contactMap.getValue(phone).accountNumber = accountNumber
                }
            }
        } else {
            Log.d("ContactResponse", "No contacts registered on route")
        }

        contacts = contactMap.values.toMutableList()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        contactsAdapater = ContactsAdapater(activity!!, contacts)
        recyclerView.adapter = contactsAdapater
    }


    private fun showSendMoneyDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = activity!!.findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.enter_pin_transaction_pin, viewGroup, false)
        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(activity!!)
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()

        // val pin: String = enterPin.text.toString()
        var account = ""
        var provider = ""

        dialogView.dialogButtonPin.setOnClickListener {

            val pin: String = dialogView.enterPin.text.toString()
            if (pin.isEmpty()) {
                Toast.makeText(activity!!, "Enter pin", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            when {
                prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(Constants.SEND_MONEY_TO_MOBILE_MONEY) == 0 -> {
//                    account = parentIntent.getStringExtra(PHONE_NUMBER)
                    account = prefs.getString(Constants.PHONE_NUMBER, "").toString()
                    provider = "MPESA WALLET"
                }
                prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(Constants.SEND_MONEY_TO_BANK) == 0 -> {
                    account = prefs.getString("bankAcNumber", "").toString()
                    provider = prefs.getString("chosenBank", "").toString()
                }
                prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(Constants.SEND_MONEY_TO_ROUTE) == 0 -> {
                    account = prefs.getString("walletAccountNumber", "").toString()
                    provider = "ROUTEWALLET"
                }
            }

            Log.e("FundAmountActivity", "$account P $provider")
            if (account.isEmpty()) {
                Toast.makeText(activity!!, "User not registered or haven't verified their email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val progressBar = CustomProgressBar()
            progressBar.show(activity!!, "Please Wait...")
            Constants.sendMoney(activity!!, account, binding.txtAmount.text.toString(), pin, token, provider, "Payment")
                    .setCallback { e, result ->
                        Log.e("FundAmountActivity", result.toString())
                        progressBar.dialog.dismiss()
                        if (result.has("errors")) {
                            Toast.makeText(activity!!, result.get("errors").asString, Toast.LENGTH_LONG).show()
                        } else {
                            editor.putString("Amount", binding.txtAmount.text.toString())
                            editor.apply()
                            val message = result.get("data").asJsonObject.get("message").asString
                            val intent = Intent(activity!!, FundRequestedActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("Message", message)
                            startActivity(intent)
                            alertDialog.dismiss()
                            activity!!.finish()
                        }
                    }
        }
    }

}
