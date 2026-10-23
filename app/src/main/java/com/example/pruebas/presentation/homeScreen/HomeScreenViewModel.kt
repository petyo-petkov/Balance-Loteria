package com.example.pruebas.presentation.homeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebas.domain.BalanceRepo
import com.example.pruebas.domain.LotteryDatabaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val dbRepo: LotteryDatabaseRepo,
    private val balanceRepo: BalanceRepo
) : ViewModel() {

    var state by mutableStateOf(HomeUiState())
        private set

    init {
        // Un único flow — tickets y balance se actualizan juntos en un solo state.copy
        // Evita la doble query a la DB y las dos recomposiciones separadas del original
        viewModelScope.launch {
            balanceRepo.getBalance(dbRepo.getAllTickets()).collect { newBalance ->
                state = state.copy(balance = newBalance)
            }
        }
        viewModelScope.launch {
            dbRepo.getAllTickets().collect { tickets ->
                state = state.copy(tickets = tickets.map { TicketUiMapper.toUiModel(it) })
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.DeleteAll -> deleteAll()

            is HomeIntent.ToggleDeleteDialog -> {
                state = state.copy(showDeleteDialog = !state.showDeleteDialog)
            }

            is HomeIntent.Scann -> {
                state = state.copy(isScanning = !state.isScanning)
            }

            is HomeIntent.Check -> {
                // Se ejecuta al pulsar el botón de Jackpots en el FAB
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepo.deleteAll()
        }
    }
}
