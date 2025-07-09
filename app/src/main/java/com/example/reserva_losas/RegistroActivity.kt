package com.example.reserva_losas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reserva_losas.databinding.ActivityRegistroBinding
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener {
            val dni = binding.etDni.text.toString()
            val nombre = binding.etNombre.text.toString()
            val apellido = binding.etApellido.text.toString()
            val correo = binding.etCorreo.text.toString()
            val contra = binding.etContrasena.text.toString()
            val celular = binding.etCelular.text.toString()

            if (dni.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contra.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cliente = mapOf(
                "dni_cli" to dni,
                "nomb_cli" to nombre,
                "ape_cli" to apellido,
                "correo_cli" to correo,
                "contra_cli" to contra,
                "cel_cli" to celular
            )

            CoroutineScope(Dispatchers.IO).launch {
                val respuesta = RetrofitClient.webService.agregarCliente(cliente)
                runOnUiThread {
                    if (respuesta.isSuccessful) {
                        mostrarMensajeYVolver(correo, contra)
                    } else {
                        mostrarMensaje("❌ Error al registrar usuario")
                    }
                }
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Información")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .create()
                .show()
        }
    }

    private fun mostrarMensajeYVolver(correo: String, contra: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("✅ Registro exitoso")
                .setMessage("Ahora puedes iniciar sesión con tu nueva cuenta.")
                .setCancelable(false)
                .setPositiveButton("Ir al login") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        putExtra("correo", correo)
                        putExtra("contrasena", contra)
                    }
                    startActivity(intent)
                    finish()
                }
                .create()
                .show()
        }
    }
}
