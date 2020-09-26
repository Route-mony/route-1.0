package com.beyondthehorizon.route.bottomsheets

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.Card
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.LOAD_WALLET_FROM_CARD
import com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES
import com.beyondthehorizon.route.views.CardActivity
import com.beyondthehorizon.route.views.FundAmountActivity
import com.beyondthehorizon.route.views.MobileActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_add_money.view.*
import org.json.JSONArray

class AddMoneyBottomsheet : BottomSheetDialogFragment(), TransactionModel.TransactionBottomSheetListener {
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var cardList: MutableList<Card>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_money, container, false)

        prefs = requireContext().getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = requireContext().getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        cardList = mutableListOf()
        view.card_one.visibility = View.GONE
        view.card_two.visibility = View.GONE

        try {
            var cards = JSONArray(prefs.getString(Constants.CARDS, ""))
            for (i in 0 until cards.length()) {
                var item = cards.getJSONObject(i)
                var number: String = item.get("card_number").toString()
                var expiry_date: String = item.get("expiry_date").toString()
                var clean_date: String = expiry_date.substring(0, 2) + "/" + expiry_date.substring(expiry_date.length - 2, expiry_date.length)
                var cvv: String = item.get("cvv").toString()
                var country: String = item.get("country").toString()
                cardList.add(Card(number, clean_date, cvv, country))
            }
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }
        var numberOfCards = cardList.size
        if (numberOfCards > 0) {
            var intent = Intent(requireContext(), FundAmountActivity::class.java)
            intent.putExtra(Constants.CARD_STATUS, Constants.OLD_CARD)
            view.card_one.visibility = View.VISIBLE
            var card = cardList[0].card_number
            var cardSufix = "---- ---- ---- ${card.substring(card.length - 4, card.length)}"
            view.visa.setImageResource(getCardIcon(card.get(0)))
            view.visa_number.text = cardSufix
            view.card_one.setOnClickListener {
                try {
                    if (numberOfCards > 0) {
                        intent.putExtra(Constants.CARD_NUMBER, cardList[0].card_number)
                        intent.putExtra(Constants.EXPIRY_DATE, cardList[0].expiry_date)
                        intent.putExtra(Constants.CVV_NUMBER, cardList[0].cvv)
                        intent.putExtra(Constants.COUNTRY, cardList[0].country)
                        editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                        editor.apply()
                        startActivity(intent)
                    }
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        if (numberOfCards > 1) {
            view.card_two.visibility = View.VISIBLE
            var card = cardList[1].card_number
            var cardSufix = "---- ---- ---- ${card.substring(card.length - 4, card.length)}"
            view.master_card.setImageResource(getCardIcon(card.get(0)))
            view.master_card_number.text = cardSufix
            view.card_two.setOnClickListener {
                try {
                    var intent = Intent(requireContext(), FundAmountActivity::class.java)
                    intent.putExtra(Constants.CARD_NUMBER, cardList[1].card_number)
                    intent.putExtra(Constants.EXPIRY_DATE, cardList[1].expiry_date)
                    intent.putExtra(Constants.CVV_NUMBER, cardList[1].cvv)
                    intent.putExtra(Constants.COUNTRY, cardList[1].country)
                    editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                    editor.apply()
                    startActivity(intent)
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        view.mobileCard.setOnClickListener {
            startActivity(Intent(requireContext(), MobileActivity::class.java))
        }

        view.add_card.setOnClickListener {
            startActivity(Intent(requireContext(), CardActivity::class.java))
        }


        return view
    }

    override fun transactionBottomSheetListener(amount: String?, ben_account: String?, ben_ref: String?) {
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
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