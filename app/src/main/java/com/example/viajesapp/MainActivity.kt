package com.example.viajesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var spRuta: Spinner
    private lateinit var btnIniciarViaje: Button
    private lateinit var btnContinuarViaje: Button

    private var contadorIniciar = 0
    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTitulo = findViewById(R.id.tvTitulo)
        spRuta = findViewById(R.id.spRuta)
        btnIniciarViaje = findViewById(R.id.btnIniciarViaje)
        btnContinuarViaje = findViewById(R.id.btnContinuarViaje)

        // Cargar opciones en el spinner
        val rutas = listOf("Guadalajara - Puerto vallarta", "Tepic - Mazatlán", "Colima - Manzanillo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rutas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRuta.adapter = adapter



        actualizarVisibilidad()

        btnIniciarViaje.setOnClickListener {

            val rutaSeleccionada = obtenerRutaSeleccionada()
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("ruta_actual", rutaSeleccionada)
                .apply()


            val idViaje = generarIdViaje()
            guardarIdViaje(idViaje)

            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("viaje_activo", true)
                .apply()

            val intent = Intent(this, RutaSeleccionadaActivity::class.java)
            intent.putExtra("ruta", obtenerRutaSeleccionada()) // Ajusta según cómo guardes la ruta
            startActivity(intent)
            finish()
        }


        btnContinuarViaje.setOnClickListener {
            val idViaje = obtenerIdViaje()
            if (idViaje != null) {
                startActivity(Intent(this, RegistrarPasajeroActivity::class.java))
            } else {
                Toast.makeText(this, "No hay viaje activo", Toast.LENGTH_SHORT).show()
            }
        }

        // (Opcional) Mostrar los tickets para debug
        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(this@MainActivity).ticketDao()
            val tickets = dao.obtenerTodos()
            tickets.forEach {
                android.util.Log.d("TICKET", it.toString())
            }
        }
    }

    private fun actualizarVisibilidad() {
        val viajeActivo = obtenerIdViaje() != null
        btnIniciarViaje.visibility = if (viajeActivo) Button.GONE else Button.VISIBLE
        btnContinuarViaje.visibility = if (viajeActivo) Button.VISIBLE else Button.GONE
    }

    private fun generarIdViaje(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        return sdf.format(Date())
    }

    private val KEY_RUTA_SELECCIONADA = "ruta_seleccionada"

    private fun guardarIdViaje(id: String) {
        val rutaSeleccionada = obtenerRutaSeleccionada()
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ID_VIAJE, id)
            .putString(KEY_RUTA_SELECCIONADA, rutaSeleccionada) // ← guarda la ruta
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

    private fun obtenerRutaSeleccionada(): String {
        return spRuta.selectedItem?.toString() ?: "Ruta desconocida"
    }


}
