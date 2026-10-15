package com.example.pruebas.presentation.jackpotScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebas.domain.WebViewRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class JackpotScreenViewModel(
    private val webViewRepo: WebViewRepo
) : ViewModel() {

    var jackpotUiState by mutableStateOf(JackpotUiState())
        private set

    init {
        getJackpot()
    }

    fun getJackpot() {
        viewModelScope.launch {
            jackpotUiState = jackpotUiState.copy(isLoading = true, error = null)
            try {
                val result = withContext(Dispatchers.IO) {
                    webViewRepo.getJackpot()
                }

                val items = result.map { json ->
                    val name = when (json["game_id"]?.jsonPrimitive?.contentOrNull) {
                        "LAPR" -> "La Primitiva"
                        "BONO" -> "Bonoloto"
                        "LNAC" -> "Loteria Nacional"
                        "ELGR" -> "El Gordo"
                        "EMIL" -> "Euromillones"
                        "EDMS" -> "Eurodreams"
                        "LOTU" -> "Lototurf"
                        "QUPL" -> "Quintuple"
                        "LAQU" -> "La Quinela"
                        "QGOL" -> "Quinigol"
                        else -> "UNKNOW"
                    }


                    JackpotState(
                        nombre = name,
                        fechaCierre = json["cierre"]?.jsonPrimitive?.contentOrNull ?: "",
                        estado = json["estado"]?.jsonPrimitive?.contentOrNull ?: "",
                        jackpot = json["premio_bote"]?.jsonPrimitive?.contentOrNull ?: "",
                        dia = json["dia_semana"]?.jsonPrimitive?.contentOrNull ?: ""
                    )
                }
                Log.d("Jackpot", items.toString())

                jackpotUiState = jackpotUiState.copy(
                    jackpots = items.sortedByDescending { it.jackpot.toDoubleOrNull() ?: 0.0 },
                    isLoading = false
                )
            } catch (e: Exception) {
                jackpotUiState = jackpotUiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}

data class JackpotUiState(
    val jackpots: List<JackpotState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@Serializable
data class JackpotState(
    val nombre: String = "",
    val fechaCierre: String = "",
    val estado: String = "",
    val jackpot: String = "",
    val dia : String = ""
)
