package com.example.pruebas.data

import com.example.pruebas.data.network.webView.MissingInfoLNAC
import com.example.pruebas.domain.Ticket

data class BarCodeData(
    val id: String,
    val numSorteo: String,
    val numDecimo: String,
    val serie: String,
    val fraccion: String
)

fun parseBarCode(rawData: String): BarCodeData? {
    val trimmed = rawData.trim()
    if (trimmed.length != 20 || !trimmed.all { it.isDigit() }) return null
    return try {
        BarCodeData(
            id = trimmed.take(20),
            numSorteo = trimmed.substring(1..3),
            numDecimo = trimmed.substring(11..15),
            serie = trimmed.substring(7..9),
            fraccion = trimmed.substring(5..6)
        )
    } catch (_: Exception) {
        null
    }
}

fun ticketFromBarCode(
    rawData: String,
    missingInfo: MissingInfoLNAC? = null
): Ticket? {
    val barCodeData = parseBarCode(rawData) ?: return null

    return Ticket(
        id = barCodeData.id,
        idSorteo = missingInfo?.idSorteo ?: "",
        cdc = missingInfo?.idSorteo?.take(5) ?: "",
        gameId = "LNAC",
        name = "Loteria Nacional",
        numeroSorteo = barCodeData.numSorteo,
        fecha = missingInfo?.fecha ?: "",
        gameStatus = missingInfo?.gameStatus ?: "",
        office = "office?",
        numbers = emptyList(),
        prize = "0.0",
        betPrice = missingInfo?.precio ?: "0.0",
        isWinner = false,
        numDecimo = barCodeData.numDecimo,
        serie = barCodeData.serie,
        fraccion = barCodeData.fraccion,
        cierre = missingInfo?.cierre ?: "",
        apertura = missingInfo?.apertura ?: ""
    )
}
