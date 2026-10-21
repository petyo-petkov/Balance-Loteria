package com.example.pruebas.data

import com.example.pruebas.data.network.webView.MissingInfoLNAC
import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.domain.Ticket
import com.example.pruebas.domain.WebViewRepo

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

suspend fun ticketFromQrCode(rawData: String, webViewRepo: WebViewRepo): Ticket {
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
        var missingInfo: MissingInfoLNAC? = null


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
                dreams += rawBets.map {
                    it.substringAfter(":").chunked(2).joinToString(",")
                }
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
                claves += rawBets.map {
                    it.substringAfter(":").chunked(2).joinToString(",")
                }
            }

            is LotteryGame.Nacional -> {
                missingInfo = webViewRepo.getMissingInfoLNAC(numeroSorteo)
                numLottery = pairs["N"]
                serie = pairs["SE"]
                fraccion = pairs["F"]
            }
            else -> {}
        }

        val isMultiple = game.isMultiple(
            numbers = numbers,
            stars = stars,
            dreams = dreams,
            clave = claves
        )

        val betCount = game.calculateBetsCount(
            numbers = numbers,
            stars = stars,
            dreams = dreams,
            clave = claves
        )

        val calculatedPrice = game.calculatePrice(
            betCount = betCount,
            hasJoker = joker != "NO" && joker != null && joker.isNotBlank(),
            sortesJugados = sorteosJugados
        ).toString()

        val info = missingInfo
        val betPrice = if (game is LotteryGame.Nacional && !info?.precio.isNullOrBlank()) {
            info.precio
        } else {
            calculatedPrice
        }

        val cierre = if (game is LotteryGame.Nacional && !info?.cierre.isNullOrBlank()) {
            info.cierre
        } else {
            fecha
        }

        val apertura = if (game is LotteryGame.Nacional) info?.apertura else null

        val finalFecha = fecha.ifEmpty { info?.fecha.orEmpty() }
        val finalGameStatus = gameStatus.ifEmpty { info?.gameStatus.orEmpty() }

        Ticket(
            id = id,
            idSorteo = "${cdc}${game.apiId}$numeroSorteo",
            gameId = game.type,
            name = game.name,
            numeroSorteo = numeroSorteo,
            cdc = cdc,
            fecha = finalFecha,
            sorteosJugados = sorteosJugados,
            gameStatus = finalGameStatus,
            office = office,
            numbers = numbers,
            prize = "0.0",
            betPrice = betPrice,
            isWinner = false,
            multiple = isMultiple,
            joker = joker,
            reintegro = reintegro,
            stars = stars,
            millon = millon,
            dreams = dreams,
            numDecimo = numLottery,
            serie = serie,
            fraccion = fraccion,
            clave = claves,
            cierre = cierre,
            apertura = apertura
        )
    } catch (e: Exception) {
        android.util.Log.e("TicketFromQr", "Error mapping ticket from QR", e)
        Ticket(id = rawData, name = "Boleto Desconocido")
    }
}

