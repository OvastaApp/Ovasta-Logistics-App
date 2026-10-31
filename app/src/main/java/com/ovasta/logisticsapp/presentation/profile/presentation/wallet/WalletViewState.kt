package com.ovasta.logisticsapp.presentation.profile.presentation.wallet

import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction

data class WalletViewState(
    // `wallet_balance` from `home`
    val balance: Double = 0.0,
    // `wallet_warning` from `home`, shown under the balance; null for none
    val walletWarning: String? = null,
    // Every page loaded so far; null until the first load succeeds
    val transactions: List<WalletTransaction>? = null,
    // Number of transactions across all pages, as reported by the backend
    val totalCount: Int = 0,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    // Reloading the balance and the first page (initial load / pull to refresh)
    val isLoading: Boolean = false,
    // Appending the next page after scrolling to the end
    val isLoadingMore: Boolean = false,
)
