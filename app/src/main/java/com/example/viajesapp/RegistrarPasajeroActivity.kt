package com.example.viajesapp

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class RegistrarPasajeroActivity : AppCompatActivity() {

    private lateinit var spOrigen: Spinner
    private lateinit var spDestino: Spinner
    private lateinit var tvPrecio: TextView
    private lateinit var btnGuardar: Button

    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"

    private val precios = mapOf(
        Pair("Ciudad A", "Ciudad B") to 20.0,
        Pair("Ciudad B", "Ciudad C") to 25.0,
        Pair("Ciudad A", "Ciudad C") to 30.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pasajero)

        spOrigen = findViewById(R.id.spOrigen)
        spDestino = findViewById(R.id.spDestino)
        tvPrecio = findViewById(R.id.tvPrecio)
        btnGuardar = findViewById(R.id.btnGuardar)

        val destinos = listOf("Ciudad A", "Ciudad B", "Ciudad C")
        spOrigen.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, destinos)
        spDestino.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, destinos)

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                actualizarPrecio()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spOrigen.onItemSelectedListener = listener
        spDestino.onItemSelectedListener = listener

        btnGuardar.setOnClickListener {
            guardarTicket()
        }
    }

    private fun actualizarPrecio() {
        val origen = spOrigen.selectedItem.toString()
        val destino = spDestino.selectedItem.toString()
        val precio = precios[Pair(origen, destino)] ?: 0.0
        tvPrecio.text = precio.toString()
    }

    private fun guardarTicket() {
        val origen = spOrigen.selectedItem.toString()
        val destino = spDestino.selectedItem.toString()
        val precio = tvPrecio.text.toString().toDoubleOrNull() ?: 0.0

        if (origen == destino) {
            Toast.makeText(this, "Origen y destino no pueden ser iguales", Toast.LENGTH_SHORT).show()
            return
        }

        val idViaje = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_VIAJE, null) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(applicationContext).ticketDao()
            val ultimoId = dao.obtenerUltimoIdPorViaje(idViaje) ?: 0
            val nuevoId = ultimoId + 1

            val ticket = TicketEntity(
                id = nuevoId,
                idViaje = idViaje,
                origen = origen,
                destino = destino,
                precio = precio,
                fecha = FechaUtils.obtenerFechaActual(),
                hora = FechaUtils.obtenerHoraActual(),
                sincronizado = false
            )

            dao.insertar(ticket)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegistrarPasajeroActivity, "Ticket guardado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
