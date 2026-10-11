package com.example.pruebas.presentation.extraDetailScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebas.data.isDrawCelebrated
import com.example.pruebas.domain.LotteryDatabaseRepo
import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.domain.WebViewRepo
import com.example.pruebas.presentation.homeScreen.TicketUiMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ExtraDetailViewModel(
    ticketId: String,
    private val webViewRepo: WebViewRepo,
    private val dbRepo: LotteryDatabaseRepo
) : ViewModel() {

    var state by mutableStateOf(ExtraDetailUiState())
        private set



    init {
        loadInfo(ticketId)

    }

    fun onIntent(intent: ExtraDetailIntent) {
        when (intent) {
            is ExtraDetailIntent.LoadInfo -> loadInfo(intent.ticketId)
        }
    }

    private fun loadInfo(ticketId: String) {
        if (state.selectedTicket?.id == ticketId && state.infoModel != null) return

        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoadingInfo = true, error = null)

            val ticket = dbRepo.getTicketById(ticketId).first()
            val uiModel = TicketUiMapper.toUiModel(ticket)
            state = state.copy(
                selectedTicket = ticket,
                ticketUiModel = uiModel
            )

            if (!isDrawCelebrated(ticket)) {
                state = state.copy(
                    isLoadingInfo = false,
                    error = "Sorteo no celebrado"
                )
                return@launch
            }

            if (ticket.gameId == "LNAC" || ticket.lotteryGame is LotteryGame.Nacional) {
                val result = webViewRepo.getExtraInfoLNAC(ticket)
                if (result != null && !isDrawCelebrated(ticket, result.cierre, result.estado)) {
                    state = state.copy(
                        infoNacional = null,
                        isLoadingInfo = false,
                        error = "Sorteo no celebrado"
                    )
                } else if (result == null) {
                    state = state.copy(
                        infoNacional = null,
                        isLoadingInfo = false,
                        error = "Sorteo no celebrado"
                    )
                } else {
                    state = state.copy(infoNacional = result, isLoadingInfo = false)
                }
            } else {
                val result = webViewRepo.getExtraInfo(ticket)
                if (result == null || !isDrawCelebrated(ticket, result.cierre)) {
                    state = state.copy(
                        infoModel = null,
                        isLoadingInfo = false,
                        error = "Sorteo no celebrado"
                    )
                } else {
                    state = state.copy(infoModel = result, isLoadingInfo = false)
                }
            }


        }
    }


}
