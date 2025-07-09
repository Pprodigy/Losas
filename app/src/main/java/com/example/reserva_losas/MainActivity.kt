package com.example.reserva_losas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reserva_losas.databinding.ActivityMainBinding
import com.example.reserva_losas.model.Losas
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var listaLosas = ArrayList<Losas>()
    private var adaptador = Adaptador()
    private var tipoUsuario: String = "cliente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        tipoUsuario = prefs.getString("tipoUsuario", "cliente") ?: "cliente"

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultar botones si es cliente
        if (tipoUsuario == "cliente") {
            binding.btnNuevaLosa.visibility = android.view.View.GONE
            binding.btnGestionarImagenes.visibility = android.view.View.GONE
            binding.btnGestionUsuarios.visibility =
                android.view.View.GONE // 👈 Ocultar este también
        }

        // 👉 Botón Gestión de Usuarios
        binding.btnGestionUsuarios.setOnClickListener {
            val intent = Intent(this, AdminUsuariosActivity::class.java)
            startActivity(intent)
        }

        binding.btnNuevaLosa.setOnClickListener {
            startActivity(Intent(this, FormularioActivity::class.java))
        }

        binding.btnGestionarImagenes.setOnClickListener {
            startActivity(Intent(this, ImagenesActivity::class.java))
        }

        binding.btnCerrarSesion.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        cargarLosas()


    binding.btnNuevaLosa.setOnClickListener {
            val intent = Intent(this, FormularioActivity::class.java)
            startActivity(intent)
        }

        binding.btnGestionarImagenes.setOnClickListener {
            val intent = Intent(this, ImagenesActivity::class.java)
            startActivity(intent)
        }

        binding.btnCerrarSesion.setOnClickListener {
            val prefsEditor = getSharedPreferences("sesion", MODE_PRIVATE).edit()
            prefsEditor.clear()
            prefsEditor.apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun cargarLosas() {
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.cargarLosas()
            runOnUiThread {
                if (rpta.isSuccessful && rpta.body() != null) {
                    listaLosas = rpta.body()!!.listaLosas
                    mostrarLosas()
                }
            }
        }
    }

    private fun mostrarLosas() {
        adaptador.setContext(this)
        adaptador.setTipoUsuario(tipoUsuario)
        adaptador.setLosas(listaLosas)

        binding.RvLosas.layoutManager = LinearLayoutManager(this)
        binding.RvLosas.adapter = adaptador

        adaptador.setEliminarItem {
            eliminar(it)
        }
    }

    private fun eliminar(losa: Losas) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Confirmar")
        ventana.setMessage("¿Desea eliminar la losa: ${losa.nombre_losa}?")
        ventana.setPositiveButton("Aceptar") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val rpta = RetrofitClient.webService.eliminarLosa(losa.id_losa)
                runOnUiThread {
                    if (rpta.isSuccessful) {
                        mostrarMensaje(rpta.body().toString())
                    }
                }
            }
        }
        ventana.setNegativeButton("Cancelar", null)
        ventana.create().show()
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



