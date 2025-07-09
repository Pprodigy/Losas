package com.example.reserva_losas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reserva_losas.adapter.ReservaAdapter
import com.example.reserva_losas.model.Cliente
import com.example.reserva_losas.model.Reserva
import com.example.reserva_losas.servicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {
    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var rvReservas: RecyclerView
    private lateinit var adapter: ReservaAdapter
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnIrAReservar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        tvNombre = findViewById(R.id.tvNombre)
        tvCorreo = findViewById(R.id.tvCorreo)
        rvReservas = findViewById(R.id.rvReservas)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnIrAReservar = findViewById(R.id.btnIrAReservar)

        rvReservas.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val userId = prefs.getInt("usuarioId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosCliente(userId)
        cargarReservas(userId)

        btnCerrarSesion.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnIrAReservar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun cargarDatosCliente(id: Int) {
        RetrofitClient.webService.obtenerCliente(id).enqueue(object : Callback<Cliente> {
            override fun onResponse(call: Call<Cliente>, response: Response<Cliente>) {
                if (response.isSuccessful) {
                    val cliente = response.body()
                    tvNombre.text = "${cliente?.nomb_cli} ${cliente?.ape_cli}"
                    tvCorreo.text = cliente?.correo_cli
                }
            }

            override fun onFailure(call: Call<Cliente>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarReservas(id: Int) {
        RetrofitClient.webService.obtenerReservas(id).enqueue(object : Callback<List<Reserva>> {
            override fun onResponse(
                call: Call<List<Reserva>>,
                response: Response<List<Reserva>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val reservas = response.body()!!
                    adapter = ReservaAdapter(reservas) { reserva ->
                        cancelarReserva(reserva.id_reserva)
                    }
                    rvReservas.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Reserva>>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelarReserva(idReserva: Int) {
        RetrofitClient.webService.cancelarReserva(idReserva).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PerfilActivity, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                    recreate()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

