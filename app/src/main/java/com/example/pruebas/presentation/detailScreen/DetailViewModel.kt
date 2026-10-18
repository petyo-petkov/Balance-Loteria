package com.example.pruebas.presentation.detailScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebas.data.isDrawCelebrated
import com.example.pruebas.data.parsePrize
import com.example.pruebas.domain.LotteryDatabaseRepo
import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.domain.WebViewRepo
import com.example.pruebas.presentation.homeScreen.TicketUiMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(
    ticketId: String,
    private val webViewRepo: WebViewRepo,
    private val dbRepo: LotteryDatabaseRepo
) : ViewModel() {

    var state by mutableStateOf(DetailUiState())
        private set

    private var loadTicketJob: Job? = null

    init {
        loadTicket(ticketId)
    }

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadTicket -> loadTicket(intent.ticketId)
            is DetailIntent.CheckTicket -> checkTicket()
            is DetailIntent.DeleteTicket -> deleteTicket()
            is DetailIntent.ToggleDeleteDialog -> {
                state = state.copy(showDeleteDialog = !state.showDeleteDialog)
            }
            is DetailIntent.ToggleCheckDialog -> {
                state = state.copy(showCheckDialog = !state.showCheckDialog)
            }
        }
    }

    private fun loadTicket(ticketId: String) {
        if (state.ticketUiModel?.ticket?.id == ticketId && loadTicketJob?.isActive == true) {
            return
        }
        loadTicketJob?.cancel()
        state = DetailUiState()

        loadTicketJob = viewModelScope.launch {
            dbRepo.getTicketById(ticketId).collect { ticket ->
                state = state.copy(ticketUiModel = TicketUiMapper.toUiModel(ticket))
            }
        }
    }

    private fun checkTicket() {
        val ticket = state.ticketUiModel?.ticket ?: return

        if (!isDrawCelebrated(ticket)) {
            state = state.copy(
                isLoadingCheck = false,
                showCheckDialog = true,
                error = "Sorteo no celebrado"
            )
            return
        }

        state = state.copy(isLoadingCheck = true, showCheckDialog = true, error = null)

        viewModelScope.launch {
            var premio = 0.0
            var hasError = false
            var errorMessage: String? = null

            withContext(Dispatchers.IO) {
                if ((ticket.gameId == "LNAC" || ticket.lotteryGame is LotteryGame.Nacional) && !ticket.numDecimo.isNullOrEmpty()) {
                    try {
                        val resultLNAC = webViewRepo.getExtraInfoLNAC(ticket)
                        if (resultLNAC != null && !isDrawCelebrated(ticket, resultLNAC.cierre, resultLNAC.estado)) {
                            hasError = true
                            errorMessage = "Sorteo no celebrado"
                        } else {
                            val premioRaw = webViewRepo.getPremioLNAC(ticket)
                            premio = premioRaw.parsePrize().div(100)
                        }
                    } catch (e: Exception) {
                        hasError = true
                        Log.e("DetailViewModel", "Error en LNAC check", e)
                    }
                } else {
                    try {
                        val premioRaw = webViewRepo.getPremios(ticket)
                        premio = premioRaw.parsePrize()
                        Log.i("PREMIO", premio.toString())

                    } catch (e: Exception) {
                        hasError = true
                        errorMessage = e.message ?: "Error al comprobar el sorteo"
                        Log.e("DetailViewModel", "Error premio check", e)
                    }

                }
                if (!hasError) {
                    val updatedTicket = ticket.copy(
                        prize = premio.toString(),
                        isChecked = true
                    )
                    dbRepo.updateTicket(updatedTicket)
                }
            }

            state = state.copy(
                isLoadingCheck = false,
                error = if (hasError) errorMessage else null
            )
        }
    }

    private fun deleteTicket() {
        val ticket = state.ticketUiModel?.ticket ?: return
        viewModelScope.launch(Dispatchers.IO) {
            dbRepo.deleteTicket(ticket)
        }
    }
}
