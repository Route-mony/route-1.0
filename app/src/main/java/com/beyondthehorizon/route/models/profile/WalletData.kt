package com.beyondthehorizon.route.models.profile

import com.google.gson.annotations.SerializedName

data class WalletData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("wallet_account") val walletAccount: String? = null,
    @SerializedName("wallet_card") val walletCard: String? = null,
    @SerializedName("wallet_card_serno") val walletCardSerno: String? = null,
    @SerializedName("available_balance") val availableBalance: String? = null,
    @SerializedName("ledger_balance") val ledgerBalance: String? = null
)