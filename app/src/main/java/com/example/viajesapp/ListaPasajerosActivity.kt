package com.example.viajesapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class ListaPasajerosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter

    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pasajeros)

        recyclerView = findViewById(R.id.recyclerTickets)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarTickets()
    }

    private fun cargarTickets() {
        val idViaje = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_VIAJE, null)

        if (idViaje == null) {
            Toast.makeText(this, "No hay un viaje activo", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val tickets = db.ticketDao().obtenerTicketsPorViaje(idViaje)

            withContext(Dispatchers.Main) {
                adapter = TicketAdapter(tickets)
                recyclerView.adapter = adapter
            }
        }
    }
}
