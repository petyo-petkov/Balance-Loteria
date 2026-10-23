package com.example.pruebas.data.network.webView.urlsGetPremio

import com.example.pruebas.domain.Ticket
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

fun urlPremioEMIL(ticket: Ticket): String {
    val modalidad = if (ticket.multiple || ticket.lotteryGame.isMultiple(ticket.numbers, ticket.stars, ticket.dreams, ticket.clave)) "multiple" else "simple"

    return URLBuilder("https://www.loteriasyapuestas.es").apply {
        appendPathSegments("es", "resultados", "euromillones", "comprobar")
        parameters.append("drawId", ticket.idSorteo)
        parameters.append("modalidad", modalidad)

        ticket.numbers.forEachIndexed { index, combinacion ->
            val numeros = formatNumbersString(combinacion)
            if (numeros.isNotBlank()) {
                parameters.append("bloque${index + 1}", numeros)
            }

            val estrella = ticket.stars?.getOrNull(index)
            if (!estrella.isNullOrBlank()) {
                val estrellasFormatted = formatNumbersString(estrella)
                if (estrellasFormatted.isNotBlank()) {
                    parameters.append("estrellas${index + 1}", estrellasFormatted)
                }
            }
        }
    }.buildString()
}

/**
 *
 * Ejemplo consulta apuesta simple
 * https://www.loteriasyapuestas.es/es/resultados/euromillones/comprobar?drawId=1254702089&modalidad=simple&bloque1=08y20y24y01y29&estrellas1=08y09&bloque2=07y11y22y36y44&estrellas2=07y08&bloque3=30y50y14y36y43&estrellas3=01y05&bloque4=03y14y16y04y05&estrellas4=01y10&bloque5=50y03y14y36y44&estrellas5=11y10
 *
 * Ejemplo consulta apuesta multiple
 *  https://www.loteriasyapuestas.es/es/resultados/euromillones/comprobar?drawId=1254702089&modalidad=multiple&bloque1=08y20y24y28y29y32&estrellas1=08y09y10
 *
 */