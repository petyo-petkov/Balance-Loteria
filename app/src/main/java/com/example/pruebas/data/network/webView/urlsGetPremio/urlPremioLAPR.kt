package com.example.pruebas.data.network.webView.urlsGetPremio

import com.example.pruebas.domain.Ticket
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

fun urlPremioLAPR(ticket: Ticket): String {
    val combinacionesParams = transformListToParams(ticket.numbers)
    val modalidad = if (ticket.multiple || ticket.lotteryGame.isMultiple(ticket.numbers, ticket.stars, ticket.dreams, ticket.clave)) "multiple" else "simple"

    return URLBuilder("https://www.loteriasyapuestas.es").apply {
        appendPathSegments("es", "resultados", "primitiva", "comprobar")
        parameters.append("drawId", ticket.idSorteo)
        parameters.append("modalidad", modalidad)

        combinacionesParams.forEach { (key, value) ->
            if (value.isNotBlank()) {
                parameters.append(key, value)
            }
        }

        ticket.reintegro?.takeIf { it.isNotBlank() }?.let { reintegro ->
            val reintegroFormatted = reintegro.trim().toIntOrNull()?.toString() ?: reintegro.trim()
            parameters.append("reintegro", reintegroFormatted)
        }

        ticket.joker?.takeIf { it.isNotBlank() && !it.equals("NO", ignoreCase = true) }?.let { joker ->
            parameters.append("joker", joker.trim())
        }
    }.buildString()
}


// LAPR
// "https://www.loteriasyapuestas.es/es/resultados/primitiva/comprobar?drawId=1005204002&modalidad=simple&bloque1=1y2y3y4y5y6&reintegro=4&joker=4224561"
//" https://www.loteriasyapuestas.es/es/resultados/primitiva/comprobar?drawId=1005204002&modalidad=simple&bloque1=29y19y2y1y5y37&bloque2=1y5y2y10y29y31&bloque3=29y19y37y2y3y20&reintegro=4&joker=4224561"
// "https://www.loteriasyapuestas.es/es/resultados/primitiva/comprobar?drawId=1005204002&modalidad=multiple&bloque1=2y3y19y21y28y17y30y4y5y6y7&reintegro=4&joker=4224561"