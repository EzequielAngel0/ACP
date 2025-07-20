package com.example.viajesapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tickets",
    primaryKeys = ["idViaje", "id"] // Clave compuesta
)
data class TicketEntity(
    val id: Int,
    val idViaje: String,
    val origen: String,
    val destino: String,
    val precio: Double,
    val fecha: String,
    val hora: String,
    val numeroCamion: String,         // ← NUEVO CAMPO
    val descuentoAplicado: String,    // ← NUEVO CAMPO
    val sincronizado: Boolean
)
