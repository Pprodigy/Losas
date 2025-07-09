package com.example.reserva_losas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reserva_losas.databinding.ActivityImagenesBinding
import com.example.reserva_losas.model.Imagen
import com.example.reserva_losas.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagenesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagenesBinding
    private lateinit var adapter: ImagenAdapter
    private var listaImagenes = listOf<Imagen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagenesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ImagenAdapter(listaImagenes) { imagen -> eliminarImagen(imagen) }
        binding.rvImagenes.layoutManager = LinearLayoutManager(this)
        binding.rvImagenes.adapter = adapter

        binding.btnAgregarImagen.setOnClickListener {
            val nombre = binding.etNombreImagen.text.toString()
            val ruta = binding.etRutaImagen.text.toString()
            if (nombre.isBlank() || ruta.isBlank()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val imagen = Imagen(0, nombre, ruta)
                agregarImagen(imagen)
            }
        }

        listarImagenes()
    }

    private fun listarImagenes() {
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.obtenerImagenes()
            runOnUiThread {
                if (rpta.isSuccessful && rpta.body() != null) {
                    listaImagenes = rpta.body()!!.listaImagenes
                    adapter.actualizarLista(listaImagenes)
                } else {
                    Toast.makeText(this@ImagenesActivity, "Error al listar imágenes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun agregarImagen(imagen: Imagen) {
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.agregarImagen(imagen)
            runOnUiThread {
                if (rpta.isSuccessful) {
                    Toast.makeText(this@ImagenesActivity, "✅ Imagen agregada", Toast.LENGTH_SHORT).show()
                    binding.etNombreImagen.text.clear()
                    binding.etRutaImagen.text.clear()
                    listarImagenes()
                } else {
                    Toast.makeText(this@ImagenesActivity, "Error al agregar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun eliminarImagen(imagen: Imagen) {
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.eliminarImagen(imagen.imagen_id)
            runOnUiThread {
                if (rpta.isSuccessful) {
                    Toast.makeText(this@ImagenesActivity, "🗑️ Imagen eliminada", Toast.LENGTH_SHORT).show()
                    listarImagenes()
                } else {
                    Toast.makeText(this@ImagenesActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
