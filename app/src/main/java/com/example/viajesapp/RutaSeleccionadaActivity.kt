package com.example.viajesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RutaSeleccionadaActivity : AppCompatActivity() {

    private lateinit var tvRutaSeleccionada: TextView
    private lateinit var btnRegistrarPasajero: Button
    private lateinit var btnFinalizarViaje: Button
    private lateinit var btnFinalizarTodo: Button

    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta_seleccionada)

        tvRutaSeleccionada = findViewById(R.id.tvRutaSeleccionada)
        btnRegistrarPasajero = findViewById(R.id.btnRegistrarPasajero)
        btnFinalizarViaje = findViewById(R.id.btnFinalizarViaje)
        btnFinalizarTodo = findViewById(R.id.btnFinalizarTodo)

        val ruta = intent.getStringExtra("ruta") ?: "Ruta desconocida"
        tvRutaSeleccionada.text = "Has seleccionado la ruta de $ruta."

        btnRegistrarPasajero.setOnClickListener {
            startActivity(Intent(this, RegistrarPasajeroActivity::class.java))
        }

        btnFinalizarViaje.setOnClickListener {
            borrarIdViaje()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnFinalizarTodo.setOnClickListener {
            val idViaje = obtenerIdViaje()
            if (idViaje != null) {
                lifecycleScope.launch {
                    val dao = AppDatabase.getDatabase(this@RutaSeleccionadaActivity).ticketDao()
                    val tickets = dao.obtenerTicketsPorViaje(idViaje)
                    if (tickets.isNotEmpty()) {
                        FirestoreHelper.sincronizarTickets(this@RutaSeleccionadaActivity)
                        ExcelExporter.exportarTickets(this@RutaSeleccionadaActivity, tickets, idViaje)
                    }
                    borrarIdViaje()
                    startActivity(Intent(this@RutaSeleccionadaActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "No hay viaje activo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerIdViaje(): String? {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_VIAJE, null)
    }

    private fun borrarIdViaje() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ID_VIAJE)
            .putBoolean("viaje_activo", false)
            .apply()
    }
}
