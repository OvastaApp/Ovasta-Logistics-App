package com.ovasta.logisticsapp.presentation.profile.presentation.wallet

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.profile.data.IWalletRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel(
    private val walletRepository: IWalletRepository,
    private val homeRepository: IHomeRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(WalletViewState())
    val viewState = _viewState.asStateFlow()

    /**
     * (Re)loads the balance (from `home`) and the first page of transactions together, keeping
     * what is on screen until they arrive. Later pages are added by [loadNextPage].
     */
    fun loadWallet() {
        if (_viewState.value.isLoading) return
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            kotlin.runCatching {
                coroutineScope {
                    val home = async { homeRepository.getHome() }
                    val firstPage = async { walletRepository.getWalletTransactions(page = FIRST_PAGE) }
                    home.await().data to firstPage.await()
                }
            }.onSuccess { (home, page) ->
                _viewState.update {
                    it.copy(
                        balance = home?.walletBalance ?: 0.0,
                        walletWarning = home?.walletWarning?.takeIf { warning -> warning.isNotBlank() },
                        transactions = page.data.orEmpty(),
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

    /** Appends the next page of transactions, if there is one and nothing is loading. */
    fun loadNextPage() {
        val state = _viewState.value
        if (state.isLoading || state.isLoadingMore || !state.hasNextPage) return
        val nextPage = state.currentPage + 1
        _viewState.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            kotlin.runCatching {
                walletRepository.getWalletTransactions(page = nextPage)
            }.onSuccess { page ->
                _viewState.update {
                    it.copy(
                        // A transaction made since the first page was loaded shifts every page
                        // by one, repeating the previous page's last transaction on this one
                        transactions = (it.transactions.orEmpty() + page.data.orEmpty())
                            .distinctBy { transaction -> transaction.id },
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

    private companion object {
        const val FIRST_PAGE = 1
    }
}
