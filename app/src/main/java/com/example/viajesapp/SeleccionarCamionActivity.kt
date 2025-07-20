package com.example.viajesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SeleccionarCamionActivity : AppCompatActivity() {

    private lateinit var spCamion: Spinner
    private lateinit var btnConfirmar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Comprobar si ya se ha seleccionado un camión antes
        val prefs = getSharedPreferences("configuracion", Context.MODE_PRIVATE)
        val camionGuardado = prefs.getString("numero_camion", null)

        if (camionGuardado != null) {
            // Ya se ha configurado el camión, ir directamente al MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_seleccionar_camion)

        spCamion = findViewById(R.id.spCamion)
        btnConfirmar = findViewById(R.id.btnConfirmar)

        // Solo opciones 6, 11 y 18
        val opcionesCamion = listOf("6", "11", "18")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesCamion)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCamion.adapter = adapter

        btnConfirmar.setOnClickListener {
            val seleccionado = spCamion.selectedItem.toString()

            // Guardar camión en SharedPreferences
            prefs.edit().putString("numero_camion", seleccionado).apply()

            Toast.makeText(this, "Camión $seleccionado configurado", Toast.LENGTH_SHORT).show()

            // Ir a MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
