package com.ovasta.logisticsapp.presentation.profile.presentation

import com.ovasta.logisticsapp.base.local.AppLanguage

data class ProfileViewState(
    val agentName: String = "",
    val phoneNumber: String = "",
    /** Outsource agents also get the Charge and Wallet pages. */
    val isOutsource: Boolean = false,
    val language: AppLanguage = AppLanguage.Arabic,
)
