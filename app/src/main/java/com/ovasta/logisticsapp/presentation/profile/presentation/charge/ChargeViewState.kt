package com.ovasta.logisticsapp.presentation.profile.presentation.charge

import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest

data class ChargeViewState(
    // Every page loaded so far; null until the first page loads
    val topUpRequests: List<TopUpRequest>? = null,
    // Number of requests across all pages, as reported by the backend
    val totalCount: Int = 0,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    // Reloading from the first page (initial load / pull to refresh)
    val isLoading: Boolean = false,
    // Appending the next page after scrolling to the end
    val isLoadingMore: Boolean = false,
    // Set once a new top-up request is sent, so the screen closes and clears its form
    val isTopUpSent: Boolean = false,
)
