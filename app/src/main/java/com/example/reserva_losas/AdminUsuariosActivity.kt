package com.example.reserva_losas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reserva_losas.adapter.UsuarioAdapter
import com.example.reserva_losas.databinding.ActivityAdminUsuariosBinding
import com.example.reserva_losas.model.Usuario
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class AdminUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUsuariosBinding
    private lateinit var adapter: UsuarioAdapter
    private val listaUsuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UsuarioAdapter(this, listaUsuarios,
            onEditarClick = { usuario ->
                val intent = Intent(this, FormUsuarioActivity::class.java).apply {
                    putExtra("id", usuario.id)
                    putExtra("nombre", usuario.nombre)
                    putExtra("apellido", usuario.apellido)
                    putExtra("dni", usuario.dni)
                    putExtra("correo", usuario.correo)
                    putExtra("celular", usuario.celular)
                    putExtra("tipo", usuario.tipo)
                }
                startActivity(intent)
            },
            onEliminarClick = { usuario ->
                mostrarDialogoEliminar(usuario)
            }
        )

        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.adapter = adapter

        binding.btnAgregarUsuario.setOnClickListener {
            startActivity(Intent(this, FormUsuarioActivity::class.java))
        }

        binding.btnIrALosas.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<List<Usuario>> = RetrofitClient.webService.obtenerUsuarios()

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        val usuarios = response.body()!!
                        listaUsuarios.clear()
                        listaUsuarios.addAll(usuarios)
                        adapter.actualizarLista(listaUsuarios)
                    } else {
                        Toast.makeText(this@AdminUsuariosActivity, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AdminUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEliminar(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de eliminar a ${usuario.nombre} ${usuario.apellido}?")
            .setPositiveButton("Sí") { _, _ -> eliminarUsuario(usuario) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarUsuario(usuario: Usuario) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (usuario.tipo == "cliente") {
                    RetrofitClient.webService.eliminarCliente(usuario.id)
                } else {
                    RetrofitClient.webService.eliminarAdmin(usuario.id)
                }

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminUsuariosActivity, "✅ Usuario eliminado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                    } else {
                        Toast.makeText(this@AdminUsuariosActivity, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AdminUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }
}

