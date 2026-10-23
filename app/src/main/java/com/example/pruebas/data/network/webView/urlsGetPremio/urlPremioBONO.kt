package com.example.pruebas.data.network.webView.urlsGetPremio

import com.example.pruebas.domain.Ticket
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments


fun urlPremioBONO(ticket: Ticket): String {
    val combinacionesParams = transformListToParams(ticket.numbers)
    val modalidad = if (ticket.multiple || ticket.lotteryGame.isMultiple(ticket.numbers, ticket.stars, ticket.dreams, ticket.clave)) "multiple" else "simple"

    return URLBuilder("https://www.loteriasyapuestas.es").apply {
        appendPathSegments("es", "resultados", "bonoloto", "comprobar")
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
    }.buildString()
}


// BONO
// https://www.loteriasyapuestas.es/es/resultados/bonoloto/comprobar?drawId=937601050&modalidad=simple&bloque1=4y5y6y23y36y15&bloque2=1y2y3y22y25y16&reintegro=4
// "https://www.loteriasyapuestas.es/es/resultados/bonoloto/comprobar?drawId=1254801311&modalidad=simple&bloque1=5y7y16y23y28y44&bloque2=2y6y12y19y27y44&bloque3=3y7y25y31y41y49&reintegro=4"