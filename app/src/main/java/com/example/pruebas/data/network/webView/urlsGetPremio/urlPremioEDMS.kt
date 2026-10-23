package com.example.pruebas.data.network.webView.urlsGetPremio

import com.example.pruebas.domain.Ticket
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

fun urlPremioEDMS(ticket: Ticket): String {
    val modalidad = if (ticket.multiple || ticket.lotteryGame.isMultiple(ticket.numbers, ticket.stars, ticket.dreams, ticket.clave)) "multiple" else "simple"

    return URLBuilder("https://www.loteriasyapuestas.es").apply {
        appendPathSegments("es", "resultados", "eurodreams", "comprobar")
        parameters.append("drawId", ticket.idSorteo)
        parameters.append("modalidad", modalidad)

        ticket.numbers.forEachIndexed { index, combinacion ->
            val numeros = formatNumbersString(combinacion)
            if (numeros.isNotBlank()) {
                parameters.append("bloque${index + 1}", numeros)
            }

            val dream = ticket.dreams?.getOrNull(index)
            if (!dream.isNullOrBlank()) {
                val dreamFormatted = dream.trim().toIntOrNull()?.toString() ?: dream.trim()
                parameters.append("numero${index + 1}", dreamFormatted)
            }
        }
    }.buildString()
}

/*
* Ejemplo consulta apuesta actual
* https://www.loteriasyapuestas.es/es/resultados/eurodreams/comprobar?drawId=1171214044&modalidad=multiple&bloque1=28y27y9y32y36y11y2y12y3y22&numero1=3
* https://www.loteriasyapuestas.es/es/resultados/eurodreams/comprobar?drawId=1176214043&modalidad=simple&bloque1=28y27y1y2y3y16&numero1=4
* https://www.loteriasyapuestas.es/es/resultados/eurodreams/comprobar?drawId=1176214043&modalidad=simple&bloque1=28y27y1y2y3y16&numero1=4&bloque2=28y27y1y2y5y34&numero2=4&bloque3=28y27y1y2y8y11&numero3=3
*/