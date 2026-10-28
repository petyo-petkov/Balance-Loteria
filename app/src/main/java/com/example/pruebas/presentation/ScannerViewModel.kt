package com.example.pruebas.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebas.domain.ProcessScannedTicketUseCase
import com.example.pruebas.domain.ScanTicketResult
import com.example.pruebas.domain.ScannerRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val scannerRepo: ScannerRepo,
    private val processScannedTicketUseCase: ProcessScannedTicketUseCase
) : ViewModel() {

    var state by mutableStateOf(ScannerUiState())
        private set

    fun onIntent(intent: ScannerIntent) {
        when (intent) {
            ScannerIntent.StartScan -> startScanning()
        }
    }

    private fun startScanning() {
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isScanning = true, error = null, message = null)
            scannerRepo.startScann().collect { data ->
                if (!data.isNullOrBlank()) {
                    when (val result = processScannedTicketUseCase(data)) {
                        is ScanTicketResult.Success -> {
                            Log.d("startScanning", "Ticket guardado: ${result.ticket}")
                            state = state.copy(isScanning = false, message = "Boleto guardado con éxito")
                        }
                        is ScanTicketResult.AlreadyExists -> {
                            Log.d("startScanning", "Ticket ya existía: ${result.ticket.id}")
                            state = state.copy(isScanning = false, message = "Este boleto ya estaba registrado")
                        }
                        is ScanTicketResult.InvalidCode -> {
                            Log.w("startScanning", "Código no válido: ${result.message}")
                            state = state.copy(isScanning = false, error = result.message)
                        }
                        is ScanTicketResult.Error -> {
                            Log.e("startScanning", "Error procesando boleto: ${result.message}")
                            state = state.copy(isScanning = false, error = result.message)
                        }
                    }
                } else {
                    state = state.copy(isScanning = false)
                }
            }
        }
    }
}
