package com.example.viajesapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ListaPasajerosActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btnMostrarSincronizados: Button
    private var mostrandoSincronizados = false

    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pasajeros)

        listView = findViewById(R.id.listView)
        btnMostrarSincronizados = findViewById(R.id.btnMostrarSincronizados)

        btnMostrarSincronizados.setOnClickListener {
            mostrandoSincronizados = !mostrandoSincronizados
            actualizarLista()
        }

        actualizarLista()
    }

    private fun actualizarLista() {
        val idViaje = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_VIAJE, null) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(applicationContext).ticketDao()
            val tickets = if (mostrandoSincronizados) {
                dao.obtenerTicketsSincronizadosPorViaje(idViaje)
            } else {
                dao.obtenerTicketsNoSincronizadosPorViaje(idViaje)
            }

            val datos = tickets.map {
                "${it.id}. ${it.origen} → ${it.destino} | \$${it.precio} | Recibido: \$${it.recibido} | Cambio: \$${it.cambio}"
            }

            withContext(Dispatchers.Main) {
                listView.adapter = ArrayAdapter(
                    this@ListaPasajerosActivity,
                    android.R.layout.simple_list_item_1,
                    datos
                )

                btnMostrarSincronizados.text = if (mostrandoSincronizados)
                    "Ocultar sincronizados"
                else
                    "Mostrar sincronizados"
            }
        }
    }
}
