package com.example.reserva_losas

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reserva_losas.databinding.ActivityFormularioBinding
import com.example.reserva_losas.model.Imagen
import com.example.reserva_losas.model.Losas
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormularioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioBinding
    private var id = 0
    private var modificar = false

    private var listaImagenes: List<Imagen> = listOf()
    private var imagenSeleccionadaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarImagenes()
        recuperarDatos()
        registrar()
    }

    private fun cargarImagenes() {
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.obtenerImagenes()
            runOnUiThread {
                if (rpta.isSuccessful && rpta.body() != null) {
                    listaImagenes = rpta.body()!!.listaImagenes

                    val nombres = listaImagenes.map { it.nombre_imagen }
                    val adapter = ArrayAdapter(this@FormularioActivity, android.R.layout.simple_spinner_item, nombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerImagenes.adapter = adapter

                    // Si está editando, seleccionar la imagen correspondiente
                    if (modificar) {
                        val idImagen = intent.getIntExtra("imagen_id", -1)
                        val posicion = listaImagenes.indexOfFirst { it.imagen_id == idImagen }
                        if (posicion != -1) binding.spinnerImagenes.setSelection(posicion)
                    }
                } else {
                    Toast.makeText(this@FormularioActivity, "Error al cargar imágenes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun recuperarDatos() {
        if (intent.hasExtra("id")) {
            modificar = true
            id = intent.getIntExtra("id", 0)

            binding.etNombre.setText(intent.getStringExtra("nombre"))
            binding.etDescripcion.setText(intent.getStringExtra("descripcion"))
            binding.etHorario.setText(intent.getStringExtra("horario"))
            binding.etDireccion.setText(intent.getStringExtra("direccion"))
            binding.etPrecio.setText(intent.getDoubleExtra("precio", 0.0).toString())
            binding.cbMantenimiento.isChecked = intent.getBooleanExtra("mantenimiento", false)

            binding.btnRegistrar.text = "Actualizar Losa"
        }
    }

    private fun registrar() {
        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val descripcion = binding.etDescripcion.text.toString()
            val horario = binding.etHorario.text.toString()
            val direccion = binding.etDireccion.text.toString()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val mantenimiento = binding.cbMantenimiento.isChecked

            val posicion = binding.spinnerImagenes.selectedItemPosition
            if (posicion == -1 || listaImagenes.isEmpty()) {
                Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            imagenSeleccionadaId = listaImagenes[posicion].imagen_id

            val losa = Losas(
                id_losa = 0,
                nombre_losa = nombre,
                descripcion_losa = descripcion,
                horario_losa = horario,
                dir_losa = direccion,
                mantenimiento_losa = mantenimiento,
                precio_hora_losa = precio,
                imagen_id = imagenSeleccionadaId,
                ruta_imagen = listaImagenes[posicion].ruta_imagen
            )

            CoroutineScope(Dispatchers.IO).launch {
                val rpta = if (!modificar) {
                    RetrofitClient.webService.agregarLosa(losa)
                } else {
                    RetrofitClient.webService.actualizarLosa(id, losa)
                }

                runOnUiThread {
                    if (rpta.isSuccessful) {
                        mostrarMensaje(rpta.body().toString())
                    } else {
                        Toast.makeText(this@FormularioActivity, "Error en la operación", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { _, _ ->
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        ventana.create().show()
    }
}


