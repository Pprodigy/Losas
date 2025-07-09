package com.example.reserva_losas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reserva_losas.R
import com.example.reserva_losas.model.Usuario

class UsuarioAdapter(
    private val context: Context,
    private var lista: List<Usuario>,
    private val onEditarClick: (Usuario) -> Unit,
    private val onEliminarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    fun actualizarLista(nuevaLista: List<Usuario>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    inner class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreUsuario)
        val tvCorreo = view.findViewById<TextView>(R.id.tvCorreoUsuario)
        val tvCelular = view.findViewById<TextView>(R.id.tvCelularUsuario)
        val tvTipo = view.findViewById<TextView>(R.id.tvTipoUsuario)
        val btnEditar = view.findViewById<Button>(R.id.btnEditarUsuario)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminarUsuario)

        fun bind(usuario: Usuario) {
            tvNombre.text = "${usuario.nombre} ${usuario.apellido}"
            tvCorreo.text = usuario.correo
            tvCelular.text = usuario.celular
            tvTipo.text = "Tipo: ${usuario.tipo}"

            btnEditar.setOnClickListener { onEditarClick(usuario) }
            btnEliminar.setOnClickListener { onEliminarClick(usuario) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fila_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size
}


