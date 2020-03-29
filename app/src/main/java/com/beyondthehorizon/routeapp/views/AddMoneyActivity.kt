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
import org.json.JSONArray

class AddMoneyActivity : AppCompatActivity(), TransactionBottomSheetListener {
    private lateinit var binding: ActivityAddMoneyBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var cardList: MutableList<Card>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_money)
        prefs = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        cardList = mutableListOf()
        binding.cardOne.visibility = View.GONE
        binding.cardTwo.visibility = View.GONE

        try {
            var cards = JSONArray(prefs.getString(CARDS, ""))
            for (i in 0 until cards.length()) {
                val item = cards.getJSONObject(i)
                var number = item.get("card_number") as Long
                var expiry_date = item.get("expiry_date") as String
                var clean_date = expiry_date.substring(0, 2) + "/" + expiry_date.substring(expiry_date.length - 3, expiry_date.length - 1)
                var cvv = item.get("cvv") as Int
                var country = item.get("country") as String
                cardList.add(Card(number, clean_date, cvv, country))
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
        var numberOfCards = cardList.size
        if (numberOfCards > 0) {
            binding.cardOne.visibility = View.VISIBLE
            var card = cardList[0].card_number.toString()
            binding.visa.setImageResource(getCardIcon(card.substring(0).toInt()))
            binding.visaNumber.text = "---- ---- ---- ${card.substring(card.length - 5, card.length - 1)}"
            binding.cardOne.setOnClickListener {
                try {
                    if (numberOfCards > 0) {
                        var intent = Intent(this, FundAmountActivity::class.java)
                        intent.putExtra(CARD_NUMBER, cardList[0].card_number)
                        intent.putExtra(EXPIRY_DATE, cardList[0].expiry_date)
                        intent.putExtra(CVV_NUMBER, cardList[0].cvv)
                        intent.putExtra(COUNTRY, cardList[0].country)
                        intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                        startActivity(intent)
                    }
                } catch (ex: Exception) {
                    Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        } else if (numberOfCards > 1) {
            binding.cardTwo.visibility = View.VISIBLE
            var card = cardList[1].card_number.toString()
            binding.masterCard.setImageResource(getCardIcon(card.substring(0).toInt()))
            binding.masterCardNumber.text = "---- ---- ---- ${card.substring(card.length - 5, card.length - 1)}"
            binding.cardTwo.setOnClickListener {
                try {
                    var intent = Intent(this, FundAmountActivity::class.java)
                    intent.putExtra(CARD_NUMBER, cardList[1].card_number)
                    intent.putExtra(EXPIRY_DATE, cardList[1].expiry_date)
                    intent.putExtra(CVV_NUMBER, cardList[1].cvv)
                    intent.putExtra(COUNTRY, cardList[1].country)
                    intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                    startActivity(intent)
                } catch (ex: Exception) {
                    Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
            binding.mobileCard.setOnClickListener {
                val transactionModel = TransactionModel()
                transactionModel.show(supportFragmentManager, "Mobile Options")
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

        fun getCardIcon(cardInitialNumber: Int): Int {
            var icon = R.drawable.ic_unkown_card
            if (cardInitialNumber == 4) {
                icon = R.drawable.visa
            } else if (cardInitialNumber == 5) {
                icon = R.drawable.mastercard
            }
            return icon
        }
    }
