package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.bottomsheets.TransactionModel
import com.beyondthehorizon.routeapp.bottomsheets.TransactionModel.TransactionBottomSheetListener
import com.beyondthehorizon.routeapp.databinding.ActivityAddMoneyBinding
import com.beyondthehorizon.routeapp.models.Card
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*
import org.json.JSONArray

class AddMoneyActivity : AppCompatActivity(), TransactionBottomSheetListener {
    private lateinit var binding: ActivityAddMoneyBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var cardList: MutableList<Card>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_money)

        btn_home.setOnClickListener {
            val intent = Intent(this@AddMoneyActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@AddMoneyActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@AddMoneyActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@AddMoneyActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        prefs = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        cardList = mutableListOf()
        binding.cardOne.visibility = View.GONE
        binding.cardTwo.visibility = View.GONE

        try {
            var cards = JSONArray(prefs.getString(CARDS, ""))
            for (i in 0 until cards.length()) {
                var item = cards.getJSONObject(i)
                var number: String = item.get("card_number").toString()
                var expiry_date:String = item.get("expiry_date").toString()
                var clean_date:String = expiry_date.substring(0, 2) + "/" + expiry_date.substring(expiry_date.length - 2, expiry_date.length)
                var cvv: String = item.get("cvv").toString()
                var country: String = item.get("country").toString()
                cardList.add(Card(number, clean_date, cvv, country))
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
        var numberOfCards = cardList.size
        if (numberOfCards > 0) {
            var intent = Intent(this, FundAmountActivity::class.java)
            intent.putExtra(CARD_STATUS, OLD_CARD)
            binding.cardOne.visibility = View.VISIBLE
            var card = cardList[0].card_number
            var cardSufix = "---- ---- ---- ${card.substring(card.length - 4, card.length)}"
            binding.visa.setImageResource(getCardIcon(card.get(0)))
            binding.visaNumber.text = cardSufix
            binding.cardOne.setOnClickListener {
                try {
                    if (numberOfCards > 0) {
                        intent.putExtra(CARD_NUMBER, cardList[0].card_number)
                        intent.putExtra(EXPIRY_DATE, cardList[0].expiry_date)
                        intent.putExtra(CVV_NUMBER, cardList[0].cvv)
                        intent.putExtra(COUNTRY, cardList[0].country)
                        editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                        editor.apply()
                        startActivity(intent)
                    }
                } catch (ex: Exception) {
                    Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (numberOfCards > 1) {
            binding.cardTwo.visibility = View.VISIBLE
            var card = cardList[1].card_number
            var cardSufix = "---- ---- ---- ${card.substring(card.length - 4, card.length)}"
            binding.masterCard.setImageResource(getCardIcon(card.get(0)))
            binding.masterCardNumber.text = cardSufix
            binding.cardTwo.setOnClickListener {
                try {
                    var intent = Intent(this, FundAmountActivity::class.java)
                    intent.putExtra(CARD_NUMBER, cardList[1].card_number)
                    intent.putExtra(EXPIRY_DATE, cardList[1].expiry_date)
                    intent.putExtra(CVV_NUMBER, cardList[1].cvv)
                    intent.putExtra(COUNTRY, cardList[1].country)
                    editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                    editor.apply()
                    startActivity(intent)
                } catch (ex: Exception) {
                    Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
            binding.mobileCard.setOnClickListener {
                startActivity(Intent(this, MobileActivity::class.java))
//                val transactionModel = TransactionModel()
//                transactionModel.show(supportFragmentManager, "Mobile Options")
            }

            binding.addCard.setOnClickListener {
                startActivity(Intent(this, CardActivity::class.java))
            }

            binding.arrowBack.setOnClickListener {
                onBackPressed()
            }
        }

        override fun transactionBottomSheetListener(amount: String?, ben_account: String?, ben_ref: String?) {
            val token = "Bearer " + prefs.getString(USER_TOKEN, "")
        }

        fun getCardIcon(cardInitialNumber: Char): Int {
            var icon = R.drawable.ic_unkown_card
            if (cardInitialNumber.compareTo('4').equals(0)) {
                icon = R.drawable.ic_visa
            } else if (cardInitialNumber.compareTo('5').equals(0)) {
                icon = R.drawable.ic_mastercard
            }
            return icon
        }
    }
