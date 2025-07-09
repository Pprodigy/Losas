package com.example.reserva_losas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reserva_losas.model.Imagen

class ImagenAdapter(
    private var lista: List<Imagen>,
    private val eliminarCallback: (Imagen) -> Unit
) : RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder>() {

    inner class ImagenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreImagen)
        val tvRuta = view.findViewById<TextView>(R.id.tvRutaImagen)
        val btnEliminar = view.findViewById<ImageButton>(R.id.btnEliminarImagen)

        fun bind(imagen: Imagen) {
            tvNombre.text = imagen.nombre_imagen
            tvRuta.text = imagen.ruta_imagen
            btnEliminar.setOnClickListener { eliminarCallback(imagen) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagen, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Imagen>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
