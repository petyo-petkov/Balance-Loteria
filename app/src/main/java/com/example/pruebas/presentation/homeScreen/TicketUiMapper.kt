package com.example.pruebas.presentation.homeScreen

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pruebas.data.parsePrize
import com.example.pruebas.domain.Ticket

object TicketUiMapper {
    fun toUiModel(ticket: Ticket): TicketUiModel {
        val prizeValue = ticket.prize.parsePrize()
        val status = when {
            !ticket.isChecked -> PrizeStatus.UNKNOWN
            prizeValue == 0.0 -> PrizeStatus.NO_PRIZE
            else -> PrizeStatus.WINNER
        }

        return TicketUiModel(
            ticket = ticket,
            height = calculateHeight(prizeValue),
            elevation = calculateElevation(prizeValue),
            lotteryColorHex = ticket.lotteryGame.colorHex,
            formattedPrize = ticket.prize,
            isChecked = ticket.isChecked,
            prizeStatus = status
        )
    }

    private fun calculateHeight(prize: Double): Int {
        return when {
            prize > 80.0 -> 200
            prize > 30.0 -> 180
            prize > 8.0 -> 160
            prize > 3.0 -> 140
            prize > 1.0 -> 120
            else -> 100
        }
    }

    private fun calculateElevation(prize: Double): Dp {
        return when {
            prize > 80.0 -> 24.dp
            prize > 30.0 -> 20.dp
            prize > 8.0 -> 16.dp
            prize > 3.0 -> 12.dp
            prize > 1.0 -> 6.dp
            prize == 0.5 -> 4.dp
            else -> 2.dp
        }
    }
}
