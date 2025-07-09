package com.example.reserva_losas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reserva_losas.databinding.ActivityLoginBinding
import com.example.reserva_losas.model.UsuarioResponse
import com.example.reserva_losas.servicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Validar sesión ya iniciada
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        if (prefs.getBoolean("logueado", false)) {
            val tipo = prefs.getString("tipoUsuario", "cliente")
            val intent = if (tipo == "admin") {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, PerfilActivity::class.java)
            }
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Rellenar campos si vienen del registro
        val correoDesdeRegistro = intent.getStringExtra("correo")
        val contrasenaDesdeRegistro = intent.getStringExtra("contrasena")

        binding.etCorreo.setText(correoDesdeRegistro ?: "")
        binding.etContrasena.setText(contrasenaDesdeRegistro ?: "")

        // Botón login
        binding.btnLogin.setOnClickListener {
            val correo = binding.etCorreo.text.toString().trim()
            val contra = binding.etContrasena.text.toString().trim()

            if (correo.isBlank() || contra.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            iniciarSesion(correo, contra)
        }

        // Botón registrarse
        binding.btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        val datos = mapOf(
            "correo" to correo,
            "contrasena" to contrasena
        )

        RetrofitClient.webService.login(datos).enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!

                    // Guardar datos en SharedPreferences
                    val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                    prefs.edit()
                        .putBoolean("logueado", true)
                        .putInt("usuarioId", usuario.id)
                        .putString("tipoUsuario", usuario.tipo)
                        .putString("nombreUsuario", usuario.nombre)
                        .apply()

                    Toast.makeText(
                        this@LoginActivity,
                        "Bienvenido ${usuario.nombre} ${usuario.apellido}",
                        Toast.LENGTH_SHORT
                    ).show()

                    val destino = if (usuario.tipo == "admin") {
                        MainActivity::class.java
                    } else {
                        PerfilActivity::class.java
                    }

                    startActivity(Intent(this@LoginActivity, destino))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


