package com.example.viajesapp

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build

class RegistrarPasajeroActivity : AppCompatActivity() {

    private lateinit var spOrigen: Spinner
    private lateinit var spDestino: Spinner
    private lateinit var tvPrecio: TextView
    private lateinit var cbAdultoMayor: CheckBox
    private lateinit var cbMenorEdad: CheckBox
    private lateinit var cbEstudiante: CheckBox
    private lateinit var cbAsociado: CheckBox
    private lateinit var btnImprimir: Button

    private val PREFS_NAME = "viaje_prefs"
    private val KEY_ID_VIAJE = "id_viaje_activo"
    private val KEY_NUMERO_CAMION = "numero_camion"

    private val preciosPorRuta = mapOf(
        "Guadalajara" to mapOf("El Grullo" to 150.0, "Puerto Vallarta" to 300.0, "Tepic" to 400.0),
        "El Grullo" to mapOf("Guadalajara" to 150.0, "Puerto Vallarta" to 180.0),
        "Puerto Vallarta" to mapOf("Guadalajara" to 300.0, "El Grullo" to 180.0),
        "Tepic" to mapOf("Guadalajara" to 400.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pasajero)

        spOrigen = findViewById(R.id.spOrigen)
        spDestino = findViewById(R.id.spDestino)
        tvPrecio = findViewById(R.id.tvPrecio)
        cbAdultoMayor = findViewById(R.id.cbAdultoMayor)
        cbMenorEdad = findViewById(R.id.cbMenorEdad)
        cbEstudiante = findViewById(R.id.cbEstudiante)
        cbAsociado = findViewById(R.id.cbAsociado)
        btnImprimir = findViewById(R.id.btnImprimir)

        val lugares = listOf("Guadalajara", "El Grullo", "Puerto Vallarta", "Tepic")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lugares)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOrigen.adapter = adapter
        spDestino.adapter = adapter

        val actualizarPrecio = {
            val origen = spOrigen.selectedItem.toString()
            val destino = spDestino.selectedItem.toString()
            val base = preciosPorRuta[origen]?.get(destino) ?: 0.0

            val descuento = when {
                cbAdultoMayor.isChecked -> 0.20
                cbMenorEdad.isChecked -> 0.15
                cbEstudiante.isChecked -> 0.10
                cbAsociado.isChecked -> 0.25
                else -> 0.0
            }

            val final = base * (1 - descuento)
            tvPrecio.text = String.format("%.2f", final)
        }

        val checkBoxes = listOf(cbAdultoMayor, cbMenorEdad, cbEstudiante, cbAsociado)
        for (cb in checkBoxes) {
            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkBoxes.filter { it != cb }.forEach { it.isChecked = false }
                    actualizarPrecio()
                } else {
                    actualizarPrecio()
                }
            }
        }

        spOrigen.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) = actualizarPrecio()
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spDestino.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) = actualizarPrecio()
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnImprimir.setOnClickListener {
            val origen = spOrigen.selectedItem.toString()
            val destino = spDestino.selectedItem.toString()
            val base = preciosPorRuta[origen]?.get(destino)

            if (origen == destino || base == null) {
                Toast.makeText(this, "Ruta inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val descuentoTipo = when {
                cbAdultoMayor.isChecked -> "Adulto mayor"
                cbMenorEdad.isChecked -> "Menor de edad"
                cbEstudiante.isChecked -> "Estudiante"
                cbAsociado.isChecked -> "Asociado"
                else -> "Ninguno"
            }

            val porcentaje = when (descuentoTipo) {
                "Adulto mayor" -> 0.20
                "Menor de edad" -> 0.15
                "Estudiante" -> 0.10
                "Asociado" -> 0.25
                else -> 0.0
            }

            val final = base * (1 - porcentaje)
            val idViaje = obtenerIdViaje() ?: run {
                Toast.makeText(this, "No hay viaje activo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val numeroCamion = obtenerNumeroCamion()

            lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(this@RegistrarPasajeroActivity).ticketDao()
                val ultimoId = dao.obtenerUltimoIdPorViaje(idViaje) ?: 0
                val nuevoId = ultimoId + 1
                val rutaGuardada = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString("ruta_actual", "$origen - $destino") ?: "$origen - $destino"

                val ticket = TicketEntity(
                    id = nuevoId,
                    idViaje = idViaje,
                    origen = origen,
                    destino = destino,
                    precio = String.format("%.2f", final).toDouble(),
                    fecha = FechaUtils.obtenerFechaActual(),
                    hora = FechaUtils.obtenerHoraActual(),
                    numeroCamion = numeroCamion,
                    descuentoAplicado = descuentoTipo,
                    sincronizado = false,
                    ruta = rutaGuardada
                )

                dao.insertar(ticket)
                imprimirTicket(origen, destino, final, nuevoId, numeroCamion)
                Toast.makeText(this@RegistrarPasajeroActivity, "Ticket registrado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun obtenerIdViaje(): String? {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ID_VIAJE, null)
    }

    private fun obtenerNumeroCamion(): String {
        return getSharedPreferences("configuracion", Context.MODE_PRIVATE)
            .getString("numero_camion", "Desconocido") ?: "Desconocido"
    }

    private fun imprimirTicket(
        origen: String,
        destino: String,
        precio: Double,
        idBoleto: Int,
        numeroCamion: String
    ) {
        val fecha = FechaUtils.obtenerFechaActual()
        val hora = FechaUtils.obtenerHoraActual()

        val textoTicket = """
            Boleto
            ------------------------------
            Camión:          $numeroCamion
            Origen:          $origen
            Destino:         $destino
            ------------------------------
            Número de boleto: $idBoleto
            Precio:           $${String.format("%.2f", precio)}
            Fecha:            $fecha
            Hora:             $hora
            ------------------------------
            Para información de su siguiente viaje
            llame a la terminal de destino.
        """.trimIndent()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        1001
                    )
                    return
                }
            }

            val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            val device = bluetoothAdapter?.bondedDevices?.firstOrNull { it.name.contains("Printer", true) }

            if (device != null) {
                val uuid = device.uuids.first().uuid
                val socket = device.createRfcommSocketToServiceRecord(uuid)
                socket.connect()

                val outputStream = socket.outputStream
                outputStream.write(textoTicket.toByteArray(Charsets.UTF_8))
                outputStream.flush()

                socket.close()

                Toast.makeText(this, "Ticket impreso correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Impresora no encontrada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al imprimir ticket: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun obtenerRutaSeleccionada(): String {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("ruta_seleccionada", "Ruta desconocida") ?: "Ruta desconocida"
    }

    private fun obtenerRutaActual(): String {
        return getSharedPreferences("viaje_prefs", Context.MODE_PRIVATE)
            .getString("ruta_actual", "Ruta desconocida") ?: "Ruta desconocida"
    }


}
