package com.example.pruebas.data.db

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface LotteryDAO {
    @Query("SELECT * FROM tickets ORDER BY fecha DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    fun getById(id: String): Flow<TicketEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lotteryEntity: TicketEntity): Long

    @Update
    suspend fun update(lotteryEntity: TicketEntity)


    @Delete
    suspend fun delete(lotteryEntity: TicketEntity)

    @Query("DELETE FROM tickets")
    suspend fun deleteAll()
}

