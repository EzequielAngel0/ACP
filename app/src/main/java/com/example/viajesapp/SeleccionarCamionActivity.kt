package com.example.viajesapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SeleccionarCamionActivity : AppCompatActivity() {

    private lateinit var spCamion: Spinner
    private lateinit var btnConfirmar: Button

    private val REQUEST_PERMISSIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar permisos antes de continuar
        if (!permisosBluetoothOtorgados()) {
            solicitarPermisosBluetooth()
            return
        }

        // Verificar si ya se ha configurado el camión
        val prefs = getSharedPreferences("configuracion", Context.MODE_PRIVATE)
        val camionGuardado = prefs.getString("numero_camion", null)

        if (camionGuardado != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_seleccionar_camion)

        spCamion = findViewById(R.id.spCamion)
        btnConfirmar = findViewById(R.id.btnConfirmar)

        val opcionesCamion = listOf("6", "11", "18")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesCamion)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCamion.adapter = adapter

        btnConfirmar.setOnClickListener {
            val seleccionado = spCamion.selectedItem.toString()
            prefs.edit().putString("numero_camion", seleccionado).apply()
            Toast.makeText(this, "Camión $seleccionado configurado", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun permisosBluetoothOtorgados(): Boolean {
        val permisos = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permisos.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        return permisos.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun solicitarPermisosBluetooth() {
        val permisos = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permisos.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        ActivityCompat.requestPermissions(this, permisos.toTypedArray(), REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (permisosBluetoothOtorgados()) {
                recreate() // Reinicia para continuar el flujo
            } else {
                Toast.makeText(this, "Se requieren permisos Bluetooth para continuar", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
