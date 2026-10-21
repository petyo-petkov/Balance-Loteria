package com.example.pruebas.domain

import com.example.pruebas.data.network.webView.MissingInfoLNAC
import com.example.pruebas.data.network.webView.webViewModels.ResultadoSorteo
import com.example.pruebas.data.network.webView.webViewModels.lnac.ResultadoSorteoLNAC
import kotlinx.serialization.json.JsonObject

data class PremioResult(
    val premio: String,
    val sorteosComprobados: Int
)

interface WebViewRepo {

    suspend fun getInfoAllGames(url: String): List<JsonObject>

    suspend fun getExtraInfo(ticket: Ticket): ResultadoSorteo?

    suspend fun getExtraInfoLNAC(ticket: Ticket): ResultadoSorteoLNAC?

    suspend fun getMissingInfoLNAC(numSorteo: String): MissingInfoLNAC?

    suspend fun getPremioLNAC(ticket: Ticket): String

    suspend fun getPremios(ticket: Ticket): PremioResult

    suspend fun getJackpot(): List<JsonObject>

}
