// AppDatabase.kt
package com.example.viajesapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TicketEntity::class], version = 12) // ✅ Incrementa la versión si haces cambios
abstract class AppDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ViajesApp" // nombre de tu base de datos
                )
                    .fallbackToDestructiveMigration() // ✅ Elimina y recrea la base de datos si cambia la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
