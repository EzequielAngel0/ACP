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
                val data = hashMapOf(
                    "idViaje" to ticket.idViaje,
                    "origen" to ticket.origen,
                    "destino" to ticket.destino,
                    "precio" to ticket.precio,
                    "recibido" to ticket.recibido,
                    "cambio" to ticket.cambio,
                    "fecha" to ticket.fecha,
                    "hora" to ticket.hora
                )

                val docRef = firestore
                    .collection("viajes")
                    .document(ticket.idViaje)
                    .collection("tickets")
                    .document(ticket.id.toString())

                try {
                    Log.d("SYNC_TEST", "Subiendo ticket: ${ticket.id} → $data")
                    docRef.set(data).await()
                    ticketDao.actualizar(ticket.copy(sincronizado = true))
                    Log.d("SYNC_TEST", "Ticket ${ticket.id} sincronizado exitosamente.")
                } catch (e: Exception) {
                    Log.e("SYNC_TEST", "Error al sincronizar ticket ${ticket.id}: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al sincronizar ticket ${ticket.id}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ticketDao.eliminarTicketsSincronizados()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Sincronización completada", Toast.LENGTH_LONG).show()
            }

        }
    }

}
