package com.example.pruebas.presentation.extraDetailScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pruebas.data.toDisplayDate
import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.presentation.Divisor
import com.example.pruebas.presentation.InfoText
import com.example.pruebas.presentation.detailScreen.detailsScreens.BonolotoDetails
import com.example.pruebas.presentation.detailScreen.detailsScreens.EurodreamsDetails
import com.example.pruebas.presentation.detailScreen.detailsScreens.EuromillonesDetails
import com.example.pruebas.presentation.detailScreen.detailsScreens.Gordo
import com.example.pruebas.presentation.detailScreen.detailsScreens.NumberCircle
import com.example.pruebas.presentation.detailScreen.detailsScreens.PrimitivaDetails

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExtraDetailScreen(
    modifier: Modifier = Modifier,
    state: ExtraDetailUiState
) {
    val selectedTicket = state.selectedTicket
    val infoModel = state.infoModel
    val isLoading = state.isLoadingInfo

    val info = infoModel

    OutlinedCard(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),

        ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator(
                    modifier = Modifier,
                    color = LoadingIndicatorDefaults.indicatorColor,
                    polygons = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons
                )
            }
        } else if (state.error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InfoText(
                    text = state.selectedTicket?.name ?: "",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(state.ticketUiModel?.lotteryColorHex ?: 1L)
                )
                Divisor()
                InfoText(
                    text = state.selectedTicket?.fecha?.toDisplayDate() ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Divisor()
                InfoText(
                    text = state.error,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            when (state.selectedTicket?.lotteryGame) {
                is LotteryGame.Nacional -> ExtraInfoNcional(state)
                else -> {}
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                InfoText(
                    text = state.selectedTicket?.name ?: info?.gameId ?: "",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(state.ticketUiModel?.lotteryColorHex ?: 1L)
                )
                Divisor()

                InfoText(text = info?.fechaSorteo?.toDisplayDate() ?: "")
                Divisor()

                InfoText(text = "Mis Combinaciones:")
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (selectedTicket?.lotteryGame) {
                        is LotteryGame.Euromillones -> EuromillonesDetails(selectedTicket)
                        is LotteryGame.Primitiva -> PrimitivaDetails(selectedTicket)
                        is LotteryGame.Eurodreams -> EurodreamsDetails(selectedTicket)
                        is LotteryGame.Bonoloto -> BonolotoDetails(selectedTicket)
                        is LotteryGame.Gordo -> Gordo(selectedTicket)
                        else -> {}
                    }
                }
                Divisor()

                InfoText("Combinación ganadora:")
                val dataSorteo = state.dataSorteo
                if (!dataSorteo.combinacion.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dataSorteo.combinacion.forEach { numero ->
                            NumberCircle(
                                number = numero,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else if (!info?.combinacion.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoText(info.combinacion)
                    }
                }
                Divisor()

                if (!dataSorteo.complimentario.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoText("Complementario:")
                        NumberCircle(
                            number = dataSorteo.complimentario,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            textColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Divisor()
                }

                if (!dataSorteo.reintegro.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val reintegroLabel = when {
                            selectedTicket?.gameId == "EDMS" || selectedTicket?.lotteryGame is LotteryGame.Eurodreams || info?.gameId == "EDMS" -> "Sueño:"
                            selectedTicket?.gameId == "ELGR" || selectedTicket?.lotteryGame is LotteryGame.Gordo || info?.gameId == "ELGR" -> "Clave:"
                            else -> "Reintegro:"
                        }
                        InfoText(reintegroLabel)
                        NumberCircle(
                            number = dataSorteo.reintegro,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            textColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Divisor()
                }

                if (!dataSorteo.estrellas.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoText("Estrellas:")
                        dataSorteo.estrellas.forEach { estrella ->
                            NumberCircle(
                                number = estrella,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Divisor()
                }

                EscrutinioSection(info?.escrutinio ?: emptyList())

            }

        }
    }
}