package com.example.viajesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var btnIniciarViaje: Button
    private lateinit var btnRegistrarPasajero: Button
    private lateinit var btnMostrarPasajeros: Button
    private lateinit var btnSincronizar: Button
    private lateinit var btnFinalizarViaje: Button
    private lateinit var tvFechaHora: TextView
    private var contadorIniciar = 0
    private var contadorFinalizar = 0

    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización
        btnIniciarViaje = findViewById(R.id.btnIniciarViaje)
        btnRegistrarPasajero = findViewById(R.id.btnRegistrarPasajero)
        btnMostrarPasajeros = findViewById(R.id.btnMostrarPasajeros)
        btnSincronizar = findViewById(R.id.btnSincronizar)
        btnFinalizarViaje = findViewById(R.id.btnFinalizarViaje)
        tvFechaHora = findViewById(R.id.tvFechaHora)

        tvFechaHora.text = obtenerFechaHoraActual()

        actualizarVisibilidad()

        btnIniciarViaje.setOnClickListener {
            contadorIniciar++
            if (contadorIniciar >= 2) {
                val idViaje = generarIdViaje()
                guardarIdViaje(idViaje)
                contadorIniciar = 0
                actualizarVisibilidad()
                Toast.makeText(this, "Viaje iniciado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Presiona otra vez para confirmar inicio", Toast.LENGTH_SHORT).show()
            }
        }

        btnFinalizarViaje.setOnClickListener {
            contadorFinalizar++
            if (contadorFinalizar >= 3) {
                val idViaje = obtenerIdViaje()
                if (idViaje != null) {
                    lifecycleScope.launch {
                        val dao = AppDatabase.getDatabase(this@MainActivity).ticketDao()
                        val tickets = dao.obtenerTicketsPorViaje(idViaje)
                        if (tickets.isNotEmpty()) {
                            ExcelExporter.exportarTickets(this@MainActivity, tickets, idViaje)
                        }
                    }
                }

                borrarIdViaje()
                contadorFinalizar = 0
                actualizarVisibilidad()
                Toast.makeText(this, "Viaje finalizado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Presiona ${3 - contadorFinalizar} vez(es) más para confirmar", Toast.LENGTH_SHORT).show()
            }
        }



        btnRegistrarPasajero.setOnClickListener {
            startActivity(Intent(this, RegistrarPasajeroActivity::class.java))
        }

        btnMostrarPasajeros.setOnClickListener {
            startActivity(Intent(this, ListaPasajerosActivity::class.java))
        }

        btnSincronizar.setOnClickListener {
            FirestoreHelper.sincronizarTickets(this)
        }

        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(this@MainActivity).ticketDao()
            val tickets = dao.obtenerTodos() // Si tienes esta función
            tickets.forEach {
                Log.d("TICKET", it.toString())
            }
        }
    }

    private fun obtenerFechaHoraActual(): String {
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        return "Fecha: $fecha  Hora: $hora"
    }

    private fun actualizarVisibilidad() {
        val viajeActivo = obtenerIdViaje() != null

        btnIniciarViaje.visibility = if (viajeActivo) Button.GONE else Button.VISIBLE
        btnRegistrarPasajero.visibility = if (viajeActivo) Button.VISIBLE else Button.GONE
        btnMostrarPasajeros.visibility = if (viajeActivo) Button.VISIBLE else Button.GONE
        btnFinalizarViaje.visibility = if (viajeActivo) Button.VISIBLE else Button.GONE
    }


    private fun generarIdViaje(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun guardarIdViaje(id: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ID_VIAJE, id)
            .apply()
    }

    private fun obtenerIdViaje(): String? {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_VIAJE, null)
    }

    private fun borrarIdViaje() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ID_VIAJE)
            .apply()
    }


}
