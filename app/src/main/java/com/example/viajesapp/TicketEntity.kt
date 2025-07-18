package com.example.viajesapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: Int,
    val idViaje: String,
    val origen: String,
    val destino: String,
    val precio: Double,
    val recibido: Double,
    val cambio: Double,
    val fecha: String,
    val hora: String,
    val sincronizado: Boolean = false
)
