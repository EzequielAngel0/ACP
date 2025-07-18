package com.example.viajesapp

import androidx.room.*

@Dao
interface TicketDao {
    @Insert
    suspend fun insertar(ticket: TicketEntity)

    @Query("SELECT * FROM tickets WHERE idViaje = :idViaje")
    suspend fun obtenerTicketsPorViaje(idViaje: String): List<TicketEntity>

    @Query("SELECT * FROM tickets WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizados(): List<TicketEntity>

    @Update
    suspend fun actualizar(ticket: TicketEntity)

    @Query("SELECT * FROM tickets")
    suspend fun obtenerTodos(): List<TicketEntity>

    @Query("DELETE FROM tickets WHERE sincronizado = 1")
    suspend fun eliminarTicketsSincronizados()

    @Query("SELECT MAX(id) FROM tickets WHERE idViaje = :idViaje")
    suspend fun obtenerUltimoIdPorViaje(idViaje: String): Int?

}
