package com.example.pruebas.data.network.webView

import android.util.Log
import android.webkit.WebView
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioBONO
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioEDMS
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioELGR
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioEMIL
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioLAPR
import com.example.pruebas.data.network.webView.webViewModels.ResultadoSorteo
import com.example.pruebas.data.network.webView.webViewModels.lnac.ResultadoSorteoLNAC
import com.example.pruebas.data.parsePrize
import com.example.pruebas.data.toApiDateFormat
import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.domain.Ticket
import com.example.pruebas.domain.WebViewRepo
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull


class WebViewRepoImpl(private val webView: WebView) : WebViewRepo {

    override suspend fun getInfoAllGames(url: String): List<JsonObject> {
        val rawString = fetchData(
            webView = webView,
            url = url,
            fetchFun = ::getRawString
        ).trim()

        if (rawString.startsWith("Error:") || rawString.isBlank()) {
            Log.e("getInfoAllGames", "Error o respuesta vacía para url $url: $rawString")
            return emptyList()
        }

        return try {
            when {
                rawString.startsWith("[") -> jsonConfig().decodeFromString<List<JsonObject>>(
                    rawString
                )

                rawString.startsWith("{") -> listOf(
                    jsonConfig().decodeFromString<JsonObject>(
                        rawString
                    )
                )

                else -> {
                    Log.e("getInfoAllGames", "Respuesta no es JSON para url $url: $rawString")
                    emptyList()
                }
            }
        } catch (e: Exception) {
            Log.e("getInfoAllGames", "Error parseando JSON de $url", e)
            emptyList()
        }
    }

    override suspend fun getExtraInfo(ticket: Ticket): ResultadoSorteo? {
        try {
            val game = ticket.lotteryGame
            val url = when (game) {
                is LotteryGame.Bonoloto -> GET_ULTIMOS_CELEBRADOS_BONO
                is LotteryGame.Primitiva -> GET_ULTIMOS_CELEBRADOS_LAPR
                is LotteryGame.Gordo -> GET_ULTIMOS_CELEBRADOS_ELGR
                is LotteryGame.Euromillones -> GET_ULTIMOS_CELEBRADOS_EMIL
                is LotteryGame.Eurodreams -> GET_ULTIMOS_CELEBRADOS_EDMS
                is LotteryGame.Nacional -> GET_ULTIMOS_CELEBRADOS_LNAC
                else -> ""
            }

            if (url.isBlank()) {
                Log.e("getExtraInfo", "URL vacía para gameType: ${ticket.gameId}")
                return null
            }

            val rawStr = fetchData(webView, url, ::getRawString).trim()

            if (rawStr.startsWith("Error:") || rawStr.isBlank()) {
                Log.e("getExtraInfo", "Error en la respuesta para url $url: $rawStr")
                return null
            }

            val sorteos = when {
                rawStr.startsWith("[") -> jsonConfig().decodeFromString<List<ResultadoSorteo>>(rawStr)
                rawStr.startsWith("{") -> listOf(jsonConfig().decodeFromString<ResultadoSorteo>(rawStr))
                else -> {
                    Log.e("getExtraInfo", "Respuesta no es JSON para url $url: $rawStr")
                    emptyList()
                }
            }

            val ticketApiDate = ticket.fecha.toApiDateFormat()
            val sorteo = sorteos.find { it.idSorteo == ticket.idSorteo }
                ?: sorteos.find {
                    it.idSorteo.endsWith(ticket.numeroSorteo) &&
                            (ticket.fecha.isBlank() ||
                                    it.fechaSorteo.toApiDateFormat() == ticketApiDate ||
                                    it.fechaSorteo.contains(ticket.fecha))
                }

            return sorteo
        } catch (e: Exception) {
            Log.e("getExtraInfo", "Error en getExtraInfo para ticket ${ticket.id}", e)
            return null
        }
    }

    override suspend fun getExtraInfoLNAC(ticket: Ticket): ResultadoSorteoLNAC? {
        try {
            val fecha = ticket.fecha.toApiDateFormat()
            if (fecha.isBlank()) {
                Log.e("getExtraInfoLNAC", "Fecha inválida para el ticket: ${ticket.fecha}")
                return null
            }

            val url =
                "https://www.loteriasyapuestas.es/servicios/buscadorSorteos?game_id=LNAC&celebrados=&fechaInicioInclusiva=$fecha&fechaFinInclusiva=$fecha"
            val rawStr = fetchData(webView, url, ::getRawString).trim()

            if (rawStr.startsWith("Error:") || rawStr.isBlank()) {
                Log.e("getExtraInfoLNAC", "Error en la respuesta: $rawStr")
                return null
            }

            return when {
                rawStr.startsWith("[") -> {
                    jsonConfig().decodeFromString<List<ResultadoSorteoLNAC>>(rawStr).firstOrNull()
                }

                rawStr.startsWith("{") -> {
                    jsonConfig().decodeFromString<ResultadoSorteoLNAC>(rawStr)
                }

                else -> {
                    Log.e("getExtraInfoLNAC", "Respuesta no válida (no es JSON): $rawStr")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("getExtraInfoLNAC", "Error en getExtraInfoLNAC", e)
        }
        return null
    }

    // Para getMissingInfoLNAC
    private fun JsonObject.matchesNumSorteo(targetNumSorteo: String): Boolean {
        val targetInt = targetNumSorteo.toIntOrNull()

        val numStr = getString("num_sorteo") ?: getString("numSorteo")
        if (numStr != null) {
            if (numStr == targetNumSorteo) return true
            if (targetInt != null && numStr.toIntOrNull() == targetInt) return true
        }

        val idStr = getString("id_sorteo") ?: getString("drawId")
        if (idStr != null) {
            val last3 = idStr.takeLast(3)
            if (last3 == targetNumSorteo) return true
            if (targetInt != null && last3.toIntOrNull() == targetInt) return true
        }

        return false
    }

    // Para TicketFromBarCode
    override suspend fun getMissingInfoLNAC(numSorteo: String): MissingInfoLNAC? {
        try {
            val proximosLNAC = getInfoAllGames(GET_PROXIMOS_LNAC)
            val proximoSorteo = proximosLNAC.find { it.matchesNumSorteo(numSorteo) }
            Log.i("PROXIMO", proximoSorteo.toString())

            if (proximoSorteo != null) {
                return MissingInfoLNAC(
                    idSorteo = proximoSorteo.getString("id_sorteo") ?: "",
                    fecha = proximoSorteo.getString("fecha") ?: "",
                    cierre = proximoSorteo.getString("cierre") ?: "",
                    apertura = proximoSorteo.getString("apertura") ?: "",
                    precio = proximoSorteo.getString("precio") ?: "0.0",
                    gameStatus = proximoSorteo.getString("estado") ?: "",
                    isCelebrado = false
                )
            }
        } catch (e: Exception) {
            Log.e("WebViewRepoImpl", "Error obtener missingInfo proximos", e)
        }

        try {
            val ultimosLNAC = getInfoAllGames(GET_ULTIMOS_CELEBRADOS_LNAC)
            val ultimoSorteo = ultimosLNAC.find { it.matchesNumSorteo(numSorteo) }
            Log.i("ULTIMO", ultimoSorteo.toString())

            if (ultimoSorteo != null) {
                return MissingInfoLNAC(
                    idSorteo = ultimoSorteo.getString("id_sorteo") ?: "",
                    fecha = ultimoSorteo.getString("fecha_sorteo") ?: "",
                    cierre = ultimoSorteo.getString("cierre") ?: "",
                    apertura = ultimoSorteo.getString("apertura") ?: "",
                    precio = ultimoSorteo.getString("precioDecimo") ?: "0.0",
                    gameStatus = ultimoSorteo.getString("estado") ?: "",
                    isCelebrado = true
                )
            }

        } catch (e: Exception) {
            Log.e("WebViewRepoImpl", "Error obtener missingInfo ultimos", e)
        }

        try {
            val urlBuscador = urlUltimosTresMeses("LNAC")
            val sorteos3Meses = getInfoAllGames(urlBuscador)
            val sorteoMatch = sorteos3Meses.find { it.matchesNumSorteo(numSorteo) }
            Log.i("BUSCADOR_3_MESES", sorteoMatch.toString())

            if (sorteoMatch != null) {
                val gameStatus = sorteoMatch.getString("estado") ?: ""
                val isCelebrado = gameStatus.equals(
                    "CELEBRADO",
                    ignoreCase = true
                ) || gameStatus.equals("COMPLETED", ignoreCase = true)
                return MissingInfoLNAC(
                    idSorteo = sorteoMatch.getString("id_sorteo") ?: "",
                    fecha = sorteoMatch.getString("fecha_sorteo") ?: "",
                    cierre = sorteoMatch.getString("cierre") ?: "",
                    apertura = sorteoMatch.getString("apertura") ?: "",
                    precio = sorteoMatch.getString("precioDecimo") ?: "0.0",
                    gameStatus = gameStatus,
                    isCelebrado = isCelebrado
                )
            }
        } catch (e: Exception) {
            Log.e("WebViewRepoImpl", "Error obtener missingInfo buscador 3 meses", e)
        }

        return null
    }

    override suspend fun getJackpot(): List<JsonObject> {
        val jackpot = fetchData(
            webView = webView,
            url = GET_JACKPOT,
            fetchFun = ::getRawString
        ).trim()

        if (jackpot.startsWith("Error:") || jackpot.isBlank()) {
            Log.e("getJackpot", "Error obteniendo jackpot: $jackpot")
            return emptyList()
        }

        return try {
            when {
                jackpot.startsWith("[") -> jsonConfig().decodeFromString<List<JsonObject>>(jackpot)
                jackpot.startsWith("{") -> listOf(jsonConfig().decodeFromString<JsonObject>(jackpot))
                else -> emptyList()
            }
        } catch (e: Exception) {
            Log.e("getJackpot", "Error parseando jackpot: $jackpot", e)
            emptyList()
        }
    }

    override suspend fun getPremios(ticket: Ticket): String {
        val game = ticket.lotteryGame
        val drawIds = multiplesSorteos(ticket)
        if (drawIds.isEmpty()) return "0.0"

        var totalPremio = 0.0

        for (drawId in drawIds) {
            val ticketForDraw = ticket.copy(idSorteo = drawId)
            val url = when (game) {
                is LotteryGame.Primitiva -> urlPremioLAPR(ticketForDraw)
                is LotteryGame.Bonoloto -> urlPremioBONO(ticketForDraw)
                is LotteryGame.Euromillones -> urlPremioEMIL(ticketForDraw)
                is LotteryGame.Eurodreams -> urlPremioEDMS(ticketForDraw)
                is LotteryGame.Gordo -> urlPremioELGR(ticketForDraw)
                else -> ""
            }

            if (url.isNotBlank()) {
                Log.i("getPremios", "Consultando sorteo $drawId: $url")
                val premioRaw = fetchData(
                    webView = webView,
                    url = url,
                    fetchFun = { getPremio(game.type) }
                ).replace("€", "").trim()

                if (!premioRaw.startsWith("Error") && premioRaw.isNotBlank()) {
                    totalPremio += premioRaw.parsePrize()
                }
            }
        }

        return totalPremio.toString()
    }

    override suspend fun getPremioLNAC(ticket: Ticket): String {
        val numDecimo = ticket.numDecimo
        val idSorteo = ticket.idSorteo

        val url = if (!numDecimo.isNullOrBlank()) urlPremioLNACPorNumero(numeroLoteria = numDecimo, idSorteo = idSorteo) else ""
        if (url.isBlank()) return "0.0"
        val data = getInfoAllGames(url)
        return data.firstOrNull()?.getString("premioEnCentimos") ?: "0.0"
    }

    private fun JsonObject.getString(key: String): String? {
        val element = this[key]
        return if (element is JsonPrimitive) element.contentOrNull else null
    }
}


private fun jsonConfig(): Json {
    return Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        isLenient = true
        allowSpecialFloatingPointValues = true
        prettyPrint = true
        useArrayPolymorphism = true
        allowStructuredMapKeys = true
    }
}

@Serializable
data class MissingInfoLNAC(
    val idSorteo: String,
    val fecha: String,
    val cierre: String,
    val apertura: String,
    val precio: String,
    val gameStatus: String,
    val isCelebrado: Boolean
)
