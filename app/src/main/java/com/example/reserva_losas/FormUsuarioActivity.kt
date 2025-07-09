package com.example.reserva_losas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reserva_losas.databinding.ActivityFormUsuarioBinding
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormUsuarioBinding
    private var usuarioId: Int? = null
    private var tipoUsuario: String = "cliente" // por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener extras si es edición
        usuarioId = intent.getIntExtra("id", -1).takeIf { it != -1 }
        val nombre = intent.getStringExtra("nombre")
        val apellido = intent.getStringExtra("apellido")
        val dni = intent.getStringExtra("dni")
        val correo = intent.getStringExtra("correo")
        val celular = intent.getStringExtra("celular")
        tipoUsuario = intent.getStringExtra("tipo") ?: "cliente"

        if (usuarioId != null) {
            binding.etNombre.setText(nombre)
            binding.etApellido.setText(apellido)
            binding.etDni.setText(dni)
            binding.etCorreo.setText(correo)
            binding.etCelular.setText(celular)
            binding.spTipo.setSelection(if (tipoUsuario == "admin") 1 else 0)
            binding.btnGuardar.text = "Actualizar"
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val dni = binding.etDni.text.toString().trim()
            val correo = binding.etCorreo.text.toString().trim()
            val celular = binding.etCelular.text.toString().trim()
            val contrasena = binding.etContrasena.text.toString().trim()
            tipoUsuario = if (binding.spTipo.selectedItemPosition == 1) "admin" else "cliente"

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || correo.isEmpty()
                || celular.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuarioId == null) {
                agregarUsuario(dni, nombre, apellido, correo, contrasena, celular, tipoUsuario)
            } else {
                editarUsuario(usuarioId!!, dni, nombre, apellido, correo, contrasena, celular, tipoUsuario)
            }
        }
    }

    private fun agregarUsuario(
        dni: String, nombre: String, apellido: String,
        correo: String, contrasena: String, celular: String, tipo: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = if (tipo == "admin") {
                RetrofitClient.webService.agregarAdmin(
                    mapOf(
                        "dni_adm" to dni, "nomb_adm" to nombre, "ape_adm" to apellido,
                        "correo_adm" to correo, "contra_adm" to contrasena, "cel_adm" to celular
                    )
                )
            } else {
                RetrofitClient.webService.agregarCliente(
                    mapOf(
                        "dni_cli" to dni, "nomb_cli" to nombre, "ape_cli" to apellido,
                        "correo_cli" to correo, "contra_cli" to contrasena, "cel_cli" to celular
                    )
                )
            }

            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@FormUsuarioActivity, "✅ Usuario guardado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@FormUsuarioActivity, "❌ Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun editarUsuario(
        id: Int, dni: String, nombre: String, apellido: String,
        correo: String, contrasena: String, celular: String, tipo: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = if (tipo == "admin") {
                RetrofitClient.webService.editarAdmin(
                    id,
                    mapOf(
                        "dni_adm" to dni, "nomb_adm" to nombre, "ape_adm" to apellido,
                        "correo_adm" to correo, "contra_adm" to contrasena, "cel_adm" to celular
                    )
                )
            } else {
                RetrofitClient.webService.editarCliente(
                    id,
                    mapOf(
                        "dni_cli" to dni, "nomb_cli" to nombre, "ape_cli" to apellido,
                        "correo_cli" to correo, "contra_cli" to contrasena, "cel_cli" to celular
                    )
                )
            }

            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@FormUsuarioActivity, "✅ Usuario actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@FormUsuarioActivity, "❌ Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


