package com.example.pruebas.domain

import com.example.pruebas.data.db.TicketEntity


data class Ticket(
    val id: String = "",
    val idSorteo: String = "",                         // A = 1321902020120280171942842258210547
    val gameId: String = "",                           // P = 2
    val name: String = "",
    val numeroSorteo: String = "",                     // S = 251......
    val cdc: String = "",                              // A = 13219....
    val fecha: String = "",                            // S = 251 08SEP26 :1
    val sorteosJugados: Int = 0,                       // S = ..........  :1
    val gameStatus: String = "",                       // W = 0
    val office: String = "",                           // T = 50050-1
    val numbers: List<String> = emptyList(),           // .1 = 040812273049.2 = 041519274249
    val prize: String = "0.0",                          // Ganancia
    val betPrice: String = "0.0",                       // Precio
    val isWinner: Boolean = false,
    val isChecked: Boolean = false,
    val cierre: String? = null,
    val apertura: String? = null,

    //Primitiva
    val joker: String? = null,                          // J = NO
    val reintegro: String? = null,                      // R = 2

    //Euromillones
    val stars: List<String>? = emptyList(),             // .1 = 021113263338:0105
    val millon: String? = null,                         // RI=11[S=07311SEP26:1,FXX92887];

    // Eurodreams
    val dreams: List<String>? = emptyList(),

    // Loteria Nacional
    val numDecimo: String? = null,                     // N = 81030
    val serie: String? = null,                          // SE = 5
    val fraccion: String? = null,                       // F = 1

    //El Gordo
    val clave: List<String>? = emptyList()
) {
    val lotteryGame: LotteryGame get() = LotteryGame.fromType(gameId)

    fun toEntity(): TicketEntity {

        return TicketEntity(
            id = id,
            idSorteo = idSorteo,
            gameId = gameId,
            name = name,
            numeroSorteo = numeroSorteo,
            cdc = cdc,
            fecha = fecha,
            sorteosJugados = sorteosJugados,
            gameStatus = gameStatus,
            office = office,
            numbers = numbers,
            prize = prize,
            betPrice = betPrice,
            isWinner = isWinner,
            isChecked = isChecked,
            cierre = cierre,
            apertura = apertura,
            joker = joker,
            reintegro = reintegro,
            stars = stars,
            millon = millon,
            dreams = dreams,
            numDecimo = numDecimo,
            serie = serie,
            fraccion = fraccion,
            clave = clave,
        )
    }
}
