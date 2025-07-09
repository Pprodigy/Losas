package com.example.reserva_losas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.reserva_losas.databinding.ActivityReservarLosaBinding
import com.example.reserva_losas.model.ReservaRequest
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReservarLosaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservarLosaBinding
    private var idLosa = 0
    private var nombreLosa = ""
    private var horarioLosa = "08:00 - 20:00"
    private var precioPorHora = 0.0

    private var horaInicioSeleccionada = 0
    private var horaFinSeleccionada = 0

    private var horaInicio = 8
    private var horaFin = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservarLosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos de la losa desde el intent
        idLosa = intent.getIntExtra("id_losa", 0)
        nombreLosa = intent.getStringExtra("nombre_losa") ?: ""
        horarioLosa = intent.getStringExtra("horario_losa") ?: "08:00 - 20:00"
        precioPorHora = intent.getDoubleExtra("precio_hora_losa", 0.0)

        binding.tvNombreLosa.text = nombreLosa

        configurarSpinners()

        binding.btnConfirmarReserva.setOnClickListener {
            mostrarResumenYConfirmar()
        }
    }

    private fun configurarSpinners() {
        val partes = horarioLosa.split(" - ")
        horaInicio = partes[0].split(":")[0].toInt()
        horaFin = partes[1].split(":")[0].toInt()

        val opcionesInicio = (horaInicio until horaFin).map {
            String.format("%02d:00", it)
        }

        val adaptadorInicio =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesInicio)
        adaptadorInicio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInicio.adapter = adaptadorInicio

        binding.spinnerInicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                horaInicioSeleccionada = horaInicio + position

                val opcionesFin = (horaInicioSeleccionada + 1..horaFin).map {
                    String.format("%02d:00", it)
                }

                val adaptadorFin = ArrayAdapter(
                    this@ReservarLosaActivity,
                    android.R.layout.simple_spinner_item,
                    opcionesFin
                )
                adaptadorFin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerFin.adapter = adaptadorFin

                if (opcionesFin.isNotEmpty()) {
                    binding.spinnerFin.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerFin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                horaFinSeleccionada = horaInicioSeleccionada + position + 1
                val cantidadHoras = horaFinSeleccionada - horaInicioSeleccionada
                val total = cantidadHoras * precioPorHora
                binding.tvTotal.text = "Total a pagar: S/ %.2f".format(total)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun mostrarResumenYConfirmar() {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val codCliente = prefs.getInt("usuarioId", -1)

        if (codCliente == -1) {
            Toast.makeText(this, "❌ Sesión inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val dp = binding.datePicker
        val fecha = "${dp.year}-${String.format("%02d", dp.month + 1)}-${String.format("%02d", dp.dayOfMonth)}"
        val horaInicio = String.format("%02d:00:00", horaInicioSeleccionada)
        val horaFin = String.format("%02d:00:00", horaFinSeleccionada)
        val cantidadHoras = horaFinSeleccionada - horaInicioSeleccionada
        val total = cantidadHoras * precioPorHora

        val mensaje = """
            📅 Fecha: $fecha
            🕐 Horario: ${horaInicioSeleccionada}:00 - ${horaFinSeleccionada}:00
            ⏱️ Horas: $cantidadHoras
            💰 Total: S/ %.2f
        """.trimIndent().format(total)

        AlertDialog.Builder(this)
            .setTitle("Confirmar Reserva")
            .setMessage(mensaje)
            .setPositiveButton("Reservar") { _, _ ->
                val reserva = ReservaRequest(
                    cod_cli = codCliente,
                    id_losa = idLosa,
                    fecha = fecha,
                    hora_inicio = horaInicio,
                    hora_fin = horaFin
                )
                verificarDisponibilidad(reserva)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun verificarDisponibilidad(reserva: ReservaRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val verificacion = RetrofitClient.webService.verificarDisponibilidad(reserva)

            runOnUiThread {
                if (verificacion.isSuccessful && verificacion.body()?.get("disponible") == false) {
                    Toast.makeText(this@ReservarLosaActivity, "❌ Ya hay una reserva en ese horario", Toast.LENGTH_LONG).show()
                } else {
                    confirmarReserva(reserva)
                }
            }
        }
    }

    private fun confirmarReserva(reserva: ReservaRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val respuesta = RetrofitClient.webService.agregarReserva(reserva)
            runOnUiThread {
                if (respuesta.isSuccessful) {
                    Toast.makeText(this@ReservarLosaActivity, "✅ Reserva registrada", Toast.LENGTH_SHORT).show()

                    // Ir directamente al perfil del cliente
                    startActivity(Intent(this@ReservarLosaActivity, PerfilActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ReservarLosaActivity, "❌ Error al registrar reserva", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

