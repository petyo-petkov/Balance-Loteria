package com.example.pruebas.data.db

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.ColumnTypeConverters


@Database(
    entities = [TicketEntity::class],
    version = 24,
    exportSchema = false
)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lotteryDao(): LotteryDAO

}


































































