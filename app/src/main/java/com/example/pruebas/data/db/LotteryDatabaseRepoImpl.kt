package com.example.pruebas.data.db

import com.example.pruebas.domain.LotteryDatabaseRepo
import com.example.pruebas.domain.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LotteryDatabaseRepoImpl(
    private val dao: LotteryDAO
) : LotteryDatabaseRepo {

    override fun getAllTickets(): Flow<List<Ticket>> {
        return dao.getAllTickets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTicketById(id: String): Flow<Ticket> {
        val ticket = dao.getById(id)
        return ticket.map { it?.toDomain() ?: Ticket() }
    }

    override suspend fun createTicket(ticket: Ticket): Boolean {
        return withContext(Dispatchers.IO) {
            val rowId = dao.insert(ticket.toEntity())
            rowId != -1L
        }
    }

    override suspend fun updateTicket(ticket: Ticket) {
        withContext(Dispatchers.IO) {
            dao.update(ticket.toEntity())
        }
    }

    override suspend fun deleteTicket(ticket: Ticket) {
        withContext(Dispatchers.IO) {
            dao.delete(ticket.toEntity())
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
    }

}
