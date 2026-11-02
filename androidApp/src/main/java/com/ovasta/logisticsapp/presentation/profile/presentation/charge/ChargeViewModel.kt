package com.ovasta.logisticsapp.presentation.profile.presentation.charge

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastEvent
import com.ovasta.logisticsapp.presentation.profile.data.IWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChargeViewModel(
    private val walletRepository: IWalletRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(ChargeViewState())
    val viewState = _viewState.asStateFlow()

    /**
     * (Re)loads the top-up requests from the first page, keeping what is on screen until it
     * arrives. Later pages are added by [loadNextPage].
     */
    fun loadTopUpRequests() {
        if (_viewState.value.isLoading) return
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            kotlin.runCatching {
                walletRepository.getTopUpRequests(page = FIRST_PAGE)
            }.onSuccess { page ->
                _viewState.update {
                    it.copy(
                        topUpRequests = page.data.orEmpty(),
                        totalCount = page.total ?: page.data.orEmpty().size,
                        currentPage = FIRST_PAGE,
                        hasNextPage = page.hasNextPage,
                        isLoading = false
                    )
                }
            }.onFailure {
                _viewState.update { state -> state.copy(isLoading = false) }
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    /** Appends the next page of top-up requests, if there is one and nothing is loading. */
    fun loadNextPage() {
        val state = _viewState.value
        if (state.isLoading || state.isLoadingMore || !state.hasNextPage) return
        val nextPage = state.currentPage + 1
        _viewState.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            kotlin.runCatching {
                walletRepository.getTopUpRequests(page = nextPage)
            }.onSuccess { page ->
                _viewState.update {
                    it.copy(
                        // A request sent since the first page was loaded shifts every page by
                        // one, repeating the previous page's last request on this one
                        topUpRequests = (it.topUpRequests.orEmpty() + page.data.orEmpty())
                            .distinctBy { request -> request.id },
                        totalCount = page.total ?: it.totalCount,
                        currentPage = nextPage,
                        hasNextPage = page.hasNextPage,
                        isLoadingMore = false
                    )
                }
            }.onFailure {
                _viewState.update { state -> state.copy(isLoadingMore = false) }
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    /** Sends a top-up request for [amount] with the transfer photo [proof], then reloads the list. */
    fun createTopUpRequest(amount: Double, proof: Uri) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                walletRepository.createTopUpRequest(amount, proof)
            }.onSuccess {
                setComposeUILoading(false)
                _viewState.update { it.copy(isTopUpSent = true) }
                emitToastEvent(ToastEvent.ResourceToastEvent(R.string.top_up_request_sent))
                loadTopUpRequests()
            }.onFailure {
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    fun onTopUpSentHandled() {
        _viewState.update { it.copy(isTopUpSent = false) }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
