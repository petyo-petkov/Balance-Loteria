package com.example.pruebas.data.network.webView

import com.example.pruebas.domain.Ticket

fun multiplesSorteos(ticket: Ticket): List<String> {
    val idLong = ticket.idSorteo.toLongOrNull() ?: return listOfNotNull(ticket.idSorteo.takeIf { it.isNotBlank() })
    val count = ticket.sorteosJugados.coerceAtLeast(1)
    val step = if (ticket.idSorteo.length == 10) 100_001L else 1L

    return List(count) { i -> (idLong + i * step).toString() }
}