package com.example.pruebas.data.network.webView.urlsGetPremio

import com.example.pruebas.domain.Ticket
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

fun urlPremioELGR(ticket: Ticket): String {
    val modalidad = if (ticket.multiple || ticket.lotteryGame.isMultiple(ticket.numbers, ticket.stars, ticket.dreams, ticket.clave)) "multiple" else "simple"

    return URLBuilder("https://www.loteriasyapuestas.es").apply {
        appendPathSegments("es", "resultados", "gordo-primitiva", "comprobar")
        parameters.append("drawId", ticket.idSorteo)
        parameters.append("modalidad", modalidad)

        ticket.numbers.forEachIndexed { index, combinacion ->
            val numeros = formatNumbersString(combinacion)
            if (numeros.isNotBlank()) {
                parameters.append("bloque${index + 1}", numeros)
            }

            val claveNum = ticket.clave?.getOrNull(index)
            if (!claveNum.isNullOrBlank()) {
                val reintegroFormatted = claveNum.trim().toIntOrNull()?.toString() ?: claveNum.trim()
                parameters.append("reintegro${index + 1}", reintegroFormatted)
            }
        }
    }.buildString()
}

/*
"https://www.loteriasyapuestas.es/es/resultados/gordo-primitiva/comprobar?drawId=940205013&modalidad=multiple&bloque1=28y27y9y32y53y11y12&reintegro1=4"
"https://www.loteriasyapuestas.es/es/resultados/gordo-primitiva/comprobar?drawId=940205013&modalidad=simple&bloque1=28y27y1y2y3&reintegro1=4"
"https://www.loteriasyapuestas.es/es/resultados/gordo-primitiva/comprobar?drawId=940205013&modalidad=simple&bloque1=28y27y1y2y3&reintegro1=4&bloque2=28y27y1y2y5&reintegro2=4&bloque3=28y27y1y2y8&reintegro3=3"

 Ejemplo consulta apuesta anterior a la 3.12.0.
 * <HOST><URL>?drawId=940205013&modalidad=multiple&bloque1=28y27y9y32y53y11y12&reintegro=4&quality=true
 * <HOST><URL>?drawId=940205013&modalidad=simple&bloque1=28y27y1y2y3&reintegro=4&quality=true
 * <HOST><URL>?drawId=940205013&modalidad=simple&bloque1=28y27y1y2y3&bloque2=28y27y1y2y5&bloque3=28y27y1y2y8&reintegro=4&quality=true
 */