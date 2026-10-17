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
                    val dataSorteoParsed = procesarCombinacion(result.combinacion, ticket.gameId)
                    state = state.copy(
                        infoModel = result,
                        dataSorteo = dataSorteoParsed,
                        isLoadingInfo = false
                    )
                }
            }
        }
    }

    fun procesarCombinacion(combRaw: String, gameId: String? = null): DataSorteo {
        return parseCombinacion(combRaw, gameId)
    }
}

fun parseCombinacion(combRaw: String, gameId: String? = null): DataSorteo {
    if (combRaw.isBlank()) return DataSorteo()

    var complementario = ""
    var reintegro = ""
    val estrellas = mutableListOf<String>()

    val cRegex = Regex("""C\(\s*([^)]+?)\s*\)""", RegexOption.IGNORE_CASE)
    cRegex.find(combRaw)?.let { matchResult ->
        complementario = matchResult.groupValues[1].trim()
    }

    val rRegex = Regex("""(?:R|Clave)\(\s*([^)]+?)\s*\)""", RegexOption.IGNORE_CASE)
    rRegex.find(combRaw)?.let { matchResult ->
        reintegro = matchResult.groupValues[1].trim()
    }

    val eRegex = Regex("""E\(\s*([^)]+?)\s*\)""", RegexOption.IGNORE_CASE)
    eRegex.findAll(combRaw).forEach { matchResult ->
        val rawStars = matchResult.groupValues[1].trim()
        val parsed = rawStars.split(Regex("""[\s,-]+""")).filter { it.isNotBlank() }
        estrellas.addAll(parsed)
    }

    val sRegex = Regex("""S\(\s*([^)]+?)\s*\)""", RegexOption.IGNORE_CASE)
    sRegex.find(combRaw)?.let { matchResult ->
        val sueno = matchResult.groupValues[1].trim()
        if (reintegro.isBlank()) {
            reintegro = sueno
        } else {
            estrellas.add(sueno)
        }
    }

    val cleanCombStr = combRaw
        .replace(cRegex, "")
        .replace(rRegex, "")
        .replace(eRegex, "")
        .replace(sRegex, "")
        .trim()

    var combinacion = cleanCombStr
        .split("-")
        .map { it.trim() }
        .filter { it.isNotBlank() && it.all { char -> char.isDigit() } }

    val game = gameId?.let { id ->
        val byType = LotteryGame.fromType(id)
        if (byType != LotteryGame.Unknown) byType else LotteryGame.fromPValue(id)
    }

    when (game) {
        is LotteryGame.Euromillones -> {
            if (estrellas.isEmpty() && combinacion.size >= 7) {
                estrellas.addAll(combinacion.takeLast(2))
                combinacion = combinacion.dropLast(2)
            } else if (combinacion.size > 5) {
                if (estrellas.isEmpty()) {
                    estrellas.addAll(combinacion.drop(5))
                }
                combinacion = combinacion.take(5)
            }
        }
        is LotteryGame.Eurodreams -> {
            if (complementario.isNotBlank() && reintegro.isBlank()) {
                reintegro = complementario
                complementario = ""
            } else if (reintegro.isBlank() && combinacion.size >= 7) {
                reintegro = combinacion.last()
                combinacion = combinacion.dropLast(1)
            } else if (combinacion.size > 6) {
                if (reintegro.isBlank()) {
                    reintegro = combinacion[6]
                }
                combinacion = combinacion.take(6)
            }
        }
        is LotteryGame.Gordo -> {
            if (complementario.isNotBlank() && reintegro.isBlank()) {
                reintegro = complementario
                complementario = ""
            } else if (reintegro.isBlank() && combinacion.size >= 6) {
                reintegro = combinacion.last()
                combinacion = combinacion.dropLast(1)
            } else if (combinacion.size > 5) {
                if (reintegro.isBlank()) {
                    reintegro = combinacion[5]
                }
                combinacion = combinacion.take(5)
            }
        }
        else -> {
            val upperType = gameId?.uppercase()?.trim() ?: ""
            if (upperType == "EMIL" || upperType == "EUROMILLONES" || upperType == "7" || upperType == "02") {
                if (estrellas.isEmpty() && combinacion.size >= 7) {
                    estrellas.addAll(combinacion.takeLast(2))
                    combinacion = combinacion.dropLast(2)
                } else if (combinacion.size > 5) {
                    if (estrellas.isEmpty()) {
                        estrellas.addAll(combinacion.drop(5))
                    }
                    combinacion = combinacion.take(5)
                }
            } else if (upperType == "EDMS" || upperType == "EURODREAMS" || upperType == "14") {
                if (complementario.isNotBlank() && reintegro.isBlank()) {
                    reintegro = complementario
                    complementario = ""
                } else if (reintegro.isBlank() && combinacion.size >= 7) {
                    reintegro = combinacion.last()
                    combinacion = combinacion.dropLast(1)
                } else if (combinacion.size > 6) {
                    if (reintegro.isBlank()) {
                        reintegro = combinacion[6]
                    }
                    combinacion = combinacion.take(6)
                }
            } else if (upperType == "ELGR" || upperType == "GORDO" || upperType == "EL GORDO" || upperType == "4" || upperType == "05") {
                if (complementario.isNotBlank() && reintegro.isBlank()) {
                    reintegro = complementario
                    complementario = ""
                } else if (reintegro.isBlank() && combinacion.size >= 6) {
                    reintegro = combinacion.last()
                    combinacion = combinacion.dropLast(1)
                } else if (combinacion.size > 5) {
                    if (reintegro.isBlank()) {
                        reintegro = combinacion[5]
                    }
                    combinacion = combinacion.take(5)
                }
            }
        }
    }

    return DataSorteo(
        combinacion = combinacion,
        reintegro = reintegro,
        complimentario = complementario,
        estrellas = estrellas
    )
}
