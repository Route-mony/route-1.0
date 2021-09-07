package com.beyondthehorizon.route.models.common

import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.models.SendMoneyModel
import java.io.Serializable

data class TransactionData(
    var message: String? = "",
    var process: Int? = 0,
    var typeOfActivity: String? = "",
    var amount: String? = "",
    var phone: String? = "",
    var contact: Contact? = null,
    var provider: String? = "",
    var fetchAllContacts: Boolean? = false,
    var sendMoneyModel: SendMoneyModel? = SendMoneyModel()
) : Serializable {
    fun getTransactionMessage(): String {
        return message!!
    }

    fun getProcessNo(): Int {
        return process!!
    }

    fun getTransactionAmount(): String {
        return amount!!
    }

    fun getActivityType(): String {
        return typeOfActivity!!
    }

    fun getTransactionPhone(): String {
        return phone!!
    }

    fun getToOrFromContact(): Contact {
        return contact!!
    }

    fun getFetchAllContact(): Boolean {
        return fetchAllContacts!!
    }

    fun getWalletProvider(): String {
        return provider!!
    }

    fun getMoneyModel(): SendMoneyModel {
        return sendMoneyModel!!
    }
}
