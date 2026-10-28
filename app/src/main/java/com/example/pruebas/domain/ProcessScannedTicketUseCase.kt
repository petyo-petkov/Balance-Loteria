package com.example.pruebas.domain

import com.example.pruebas.data.getGameFromQr
import com.example.pruebas.data.getNumeroSorteoFromQr
import com.example.pruebas.data.parseBarCode
import com.example.pruebas.data.ticketFromBarCode
import com.example.pruebas.data.ticketFromQrCode

sealed interface ScanTicketResult {
    data class Success(val ticket: Ticket) : ScanTicketResult
    data class AlreadyExists(val ticket: Ticket) : ScanTicketResult
    data class InvalidCode(val message: String = "Código no válido o no reconocido") : ScanTicketResult
    data class Error(val message: String) : ScanTicketResult
}

class ProcessScannedTicketUseCase(
    private val webViewRepo: WebViewRepo,
    private val dbRepo: LotteryDatabaseRepo
) {
    suspend operator fun invoke(rawData: String): ScanTicketResult {
        val trimmed = rawData.trim()
        if (trimmed.isBlank()) {
            return ScanTicketResult.InvalidCode("Código vacío")
        }

        return try {
            val ticket = when {
                isQrCode(trimmed) -> processQr(trimmed)
                isBarCode(trimmed) -> processBarCode(trimmed)
                else -> null
            } ?: return ScanTicketResult.InvalidCode()

            val wasInserted = dbRepo.createTicket(ticket)
            if (wasInserted) {
                ScanTicketResult.Success(ticket)
            } else {
                ScanTicketResult.AlreadyExists(ticket)
            }
        } catch (e: Exception) {
            ScanTicketResult.Error(e.message ?: "Error al procesar el boleto")
        }
    }

    private fun isQrCode(data: String): Boolean {
        return data.contains("A=") && data.contains("P=")
    }

    private fun isBarCode(data: String): Boolean {
        return parseBarCode(data) != null
    }

    private suspend fun processQr(data: String): Ticket? {
        val game = getGameFromQr(data)
        val missingInfo = if (game is LotteryGame.Nacional) {
            val numSorteo = getNumeroSorteoFromQr(data)
            if (!numSorteo.isNullOrBlank()) {
                webViewRepo.getMissingInfoLNAC(numSorteo)
            } else null
        } else {
            null
        }
        return ticketFromQrCode(data, missingInfo)
    }

    private suspend fun processBarCode(data: String): Ticket? {
        val barCodeData = parseBarCode(data) ?: return null
        val missingInfo = webViewRepo.getMissingInfoLNAC(barCodeData.numSorteo)
        return ticketFromBarCode(data, missingInfo)
    }
}
