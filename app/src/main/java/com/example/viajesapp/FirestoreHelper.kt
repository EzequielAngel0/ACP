package com.example.viajesapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

object FirestoreHelper {

    fun sincronizarTickets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val ticketDao = db.ticketDao()
            val ticketsNoSincronizados = ticketDao.obtenerNoSincronizados()

            Log.d("SYNC_TEST", "Tickets sin sincronizar encontrados: ${ticketsNoSincronizados.size}")
            if (ticketsNoSincronizados.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No hay tickets por sincronizar", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val firestore = FirebaseFirestore.getInstance()

            for (ticket in ticketsNoSincronizados) {
                val ticketIdFirestore = "${ticket.idViaje}_${ticket.id}"
                val ruta = "${ticket.origen} - ${ticket.destino}"

                val data = hashMapOf(
                    "ticketId" to ticketIdFirestore,
                    "idViaje" to ticket.idViaje,
                    "origen" to ticket.origen,
                    "destino" to ticket.destino,
                    "ruta" to ruta,  // NUEVO
                    "precio" to ticket.precio,
                    "fecha" to ticket.fecha,
                    "hora" to ticket.hora,
                    "numeroCamion" to ticket.numeroCamion,             // NUEVO
                    "descuentoAplicado" to ticket.descuentoAplicado    // NUEVO
                )

                try {
                    firestore
                        .collection("viajes")
                        .document(ticket.idViaje)
                        .collection("tickets")
                        .document(ticketIdFirestore)
                        .set(data)
                        .await()

                    ticketDao.actualizar(ticket.copy(sincronizado = true))
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al sincronizar ticket ID ${ticket.id}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Sincronización completada", Toast.LENGTH_LONG).show()
            }
        }
    }
}
