package com.example.pruebas.presentation.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.pruebas.data.parsePrize
import com.example.pruebas.data.toDisplayDate
import com.example.pruebas.domain.Ticket

@Composable
fun TicketUI(
    uiModel: TicketUiModel,
    onClick: (Ticket) -> Unit
) {
    val ticket = uiModel.ticket
    val lotteryColor = Color(uiModel.lotteryColorHex)
    val prizeBackgroundColor = when(uiModel.prizeStatus){
        PrizeStatus.NO_PRIZE -> MaterialTheme.colorScheme.secondaryContainer
        PrizeStatus.WINNER -> MaterialTheme.colorScheme.primaryContainer
        PrizeStatus.UNKNOWN ->  MaterialTheme.colorScheme.errorContainer
    }

    ElevatedCard(
        onClick = { onClick(ticket) },
        modifier = Modifier
            .fillMaxWidth()
            .height(uiModel.height.dp),

        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(containerColor = prizeBackgroundColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = uiModel.elevation)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Date
            Fila(
                text = ticket.fecha.toDisplayDate(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Game
            Fila(
                text = ticket.name,
                style = MaterialTheme.typography.titleLargeEmphasized,
                color = lotteryColor
            )
            // Varios Sorteos
            if (ticket.sorteosJugados > 1) {
                val comprobados = when {
                    ticket.sorteosComprobados > 0 -> ticket.sorteosComprobados
                    ticket.isChecked -> ticket.sorteosJugados
                    else -> 0
                }
                Fila(
                    text = "Sorteos: $comprobados/${ticket.sorteosJugados}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }


            // Precio - Premio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Fila(
                    text = "${ticket.betPrice} €",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (ticket.isChecked) {

                    val prizeTextColor = when (uiModel.prizeStatus) {
                        PrizeStatus.NO_PRIZE -> MaterialTheme.colorScheme.error
                        PrizeStatus.WINNER -> Color(0xFF388E3C)
                        PrizeStatus.UNKNOWN -> {
                            if (ticket.prize.parsePrize() > 0.0) Color(0xFF388E3C)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    }
                    Fila(
                        text = "${ticket.prize} €",
                        style = MaterialTheme.typography.bodyLarge,
                        color = prizeTextColor

                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.QuestionMark,
                        contentDescription = null,
                        tint = Color(0xFFFBC02D)
                    )
                }
            }
        }

    }
}

@Composable
fun Fila(text: String, style: TextStyle, color: Color) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = style,
            color = color
        )
    }
}