package com.example.pruebas.presentation.extraDetailScreen

import com.example.pruebas.data.network.webView.webViewModels.ResultadoSorteo
import com.example.pruebas.data.network.webView.webViewModels.lnac.ResultadoSorteoLNAC
import com.example.pruebas.domain.Ticket
import com.example.pruebas.presentation.homeScreen.TicketUiModel

data class ExtraDetailUiState(
    val selectedTicket: Ticket? = null,
    val infoModel: ResultadoSorteo? = null,
    val infoNacional: ResultadoSorteoLNAC? = null,
    val ticketUiModel: TicketUiModel? = null,
    val isLoadingInfo: Boolean = false,
    val error: String? = null
)
