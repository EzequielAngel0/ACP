package com.example.viajesapp

import java.text.SimpleDateFormat
import java.util.*

object FechaUtils {
    fun obtenerFechaActual(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun obtenerHoraActual(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}
