package com.example.pruebas.data

import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.domain.Ticket

fun parse(rawData: String): Map<String, String> {
    return try {
        val data = rawData.split(";").filter { it.contains("=") }
        val betsToken = data.find { it.startsWith(".") }
        val betsString = betsToken?.substringAfter(".")?.split(".")?.joinToString(",") ?: ""

        data.associate { pair ->
            if (pair.startsWith(".")) {
                "bets" to betsString
            } else {
                val parts = pair.split("=", limit = 2)
                if (parts.size == 2) {
                    parts[0] to parts[1]
                } else {
                    "" to ""
                }
            }
        }.filterKeys { it.isNotEmpty() }
    } catch (e: Exception) {
        android.util.Log.e("TicketFromQr", "Error parsing QR data", e)
        emptyMap()
    }
}

fun ticketFromQrCode(rawData: String): Ticket {
    return try {
        val pairs = parse(rawData)

        val id = pairs["A"] ?: rawData
        val numeroSorteo = pairs["S"]?.take(3) ?: ""
        val cdc = pairs["A"]?.take(5) ?: ""
        val gameStatus = pairs["W"] ?: ""
        val office = pairs["T"] ?: ""
        val fecha = pairs["S"]?.substringBefore(":")?.takeLast(7)?.toStoreDate() ?: ""
        val sorteosJugados = pairs["S"]?.substringAfter(":")?.toInt() ?: 1
        
        val game = LotteryGame.fromPValue(pairs["P"])
        
        val rawBets = pairs["bets"]?.removeSurrounding("[", "]")
            ?.split(",")
            ?: emptyList()

        val numbers: MutableList<String> = mutableListOf()
        val stars = mutableListOf<String>()
        val dreams = mutableListOf<String>()
        var reintegro: String? = ""
        var millon: String? = ""
        var joker: String? = ""
        val claves = mutableListOf<String>()
        var numLottery: String? = ""
        var serie: String? = ""
        var fraccion: String? = ""

        when (game) {
            is LotteryGame.Primitiva -> {
                numbers += rawBets.map { it.substringAfter("=").chunked(2).joinToString(",") }
                reintegro = pairs["R"]
                joker = pairs["J"]
            }

            is LotteryGame.Bonoloto -> {
                numbers += rawBets.map { it.substringAfter("=").chunked(2).joinToString(",") }
                reintegro = pairs["R"]
            }

            is LotteryGame.Eurodreams -> {
                numbers += rawBets.map {
                    it.substringAfter("=").substringBefore(":").chunked(2).joinToString(",")
                }
                dreams += rawBets.map { it.substringAfter(":") }
            }

            is LotteryGame.Euromillones -> {
                numbers += rawBets.map {
                    it.substringAfter("=").substringBefore(":").chunked(2).joinToString(",")
                }
                stars += rawBets.map {
                    it.substringAfter(":").chunked(2).joinToString(",")
                }
                val regexMillon = """([A-Z0-9]+)]""".toRegex()
                millon = pairs["RI"]?.let { regexMillon.find(it)?.groupValues?.get(1) }
            }

            is LotteryGame.Gordo -> {
                numbers += rawBets.map {
                    it.substringAfter("=").substringBefore(":").chunked(2).joinToString(",")
                }
                claves += rawBets.map { it.substringAfter(":") }
            }

            is LotteryGame.Nacional -> {
                numLottery = pairs["N"]
                serie = pairs["SE"]
                fraccion = pairs["F"]
            }
            else -> {}
        }

        val betPrice = game.calculatePrice(
            betCount = numbers.size.coerceAtLeast(1),
            hasJoker = joker != "NO" && joker != null,
        sortesJugados = sorteosJugados
        )

        Ticket(
            id = id,
            idSorteo = "${cdc}${game.apiId}$numeroSorteo",
            gameId = game.type,
            name = game.name,
            numeroSorteo = numeroSorteo,
            cdc = cdc,
            fecha = fecha,
            sorteosJugados = sorteosJugados,
            gameStatus = gameStatus,
            office = office,
            numbers = numbers,
            prize = "0.0",
            betPrice = betPrice.toString(),
            isWinner = false,
            joker = joker,
            reintegro = reintegro,
            stars = stars,
            millon = millon,
            dreams = dreams,
            numDecimo = numLottery,
            serie = serie,
            fraccion = fraccion,
            clave = claves,
            cierre = fecha,
            apertura = null
        )
    } catch (e: Exception) {
        android.util.Log.e("TicketFromQr", "Error mapping ticket from QR", e)
        Ticket(id = rawData, name = "Boleto Desconocido")
    }
}
