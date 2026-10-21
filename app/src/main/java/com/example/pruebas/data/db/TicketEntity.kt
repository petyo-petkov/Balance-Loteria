package com.example.pruebas.data.db

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.example.pruebas.domain.Ticket


@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: String,
    val idSorteo: String,
    val gameId: String,
    val name: String,
    val numeroSorteo: String,
    val cdc: String,
    val fecha: String,
    val sorteosJugados: Int,
    val sorteosComprobados: Int = 0,
    val gameStatus: String,
    val office: String,
    val numbers: List<String>,
    val prize: String,
    val betPrice: String,
    val isWinner: Boolean,
    val isChecked: Boolean,
    val joker: String?,
    val reintegro: String?,
    val stars: List<String>?,
    val millon: String?,
    val dreams: List<String>?,
    val numDecimo: String?,
    val serie: String?,
    val fraccion: String?,
    val clave: List<String>?,
    val cierre: String?,
    val apertura: String?,
    val multiple: Boolean,

) {
    fun toDomain(): Ticket {
        return Ticket(
            id = id,
            idSorteo = idSorteo,
            gameId = gameId,
            name = name,
            numeroSorteo = numeroSorteo,
            cdc = cdc,
            fecha = fecha,
            sorteosJugados = sorteosJugados,
            sorteosComprobados = sorteosComprobados,
            gameStatus = gameStatus,
            office = office,
            numbers = numbers,
            prize = prize,
            betPrice = betPrice,
            isWinner = isWinner,
            isChecked = isChecked,
            joker = joker,
            reintegro = reintegro,
            stars = stars,
            millon = millon,
            dreams = dreams,
            numDecimo = numDecimo,
            serie = serie,
            fraccion = fraccion,
            clave = clave,
            cierre = cierre,
            apertura = apertura,
            multiple = multiple

        )
    }
}
