package com.example.pruebas.domain

import kotlinx.coroutines.flow.Flow

interface LotteryDatabaseRepo {

    fun getAllTickets(): Flow<List<Ticket>>

    fun getTicketById(id: String) : Flow<Ticket>

    suspend fun createTicket(ticket: Ticket): Boolean

    suspend fun updateTicket(ticket: Ticket)

    suspend fun deleteTicket(ticket: Ticket)

    suspend fun deleteAll()

}



