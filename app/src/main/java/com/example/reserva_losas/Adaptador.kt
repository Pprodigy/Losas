package com.example.reserva_losas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reserva_losas.model.Losas

class Adaptador : RecyclerView.Adapter<Adaptador.MiViewHolder>() {

    private var listaLosas = ArrayList<Losas>()
    private lateinit var context: Context
    private var tipoUsuario: String = "cliente"

    fun setContext(context: Context) {
        this.context = context
    }

    fun setLosas(losas: ArrayList<Losas>) {
        this.listaLosas = losas
    }

    fun setTipoUsuario(tipo: String) {
        this.tipoUsuario = tipo
    }

    private var eliminarItem: ((Losas) -> Unit)? = null
    fun setEliminarItem(callback: (Losas) -> Unit) {
        this.eliminarItem = callback
    }

    inner class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val filaNombreLosa = view.findViewById<TextView>(R.id.filaNombreLosa)
        private val tvDescripcionLosa = view.findViewById<TextView>(R.id.tvDescripcionLosa)
        private val tvHorarioLosa = view.findViewById<TextView>(R.id.tvHorarioLosa)
        private val tvDireccionLosa = view.findViewById<TextView>(R.id.tvDireccionLosa)
        private val tvMantenimientoLosa = view.findViewById<TextView>(R.id.tvMantenimientoLosa)
        private val tvPxHLosa = view.findViewById<TextView>(R.id.tvPxHLosa)
        private val imageView = view.findViewById<ImageView>(R.id.imageView)

        val filaEditar = view.findViewById<ImageButton>(R.id.filaEditar)
        val filaEliminar = view.findViewById<ImageButton>(R.id.filaEliminar)
        val filaReservar = view.findViewById<ImageButton>(R.id.filaReservar)

        fun rellenarFila(losa: Losas) {
            filaNombreLosa.text = losa.nombre_losa
            tvDescripcionLosa.text = losa.descripcion_losa
            tvHorarioLosa.text = losa.horario_losa
            tvDireccionLosa.text = losa.dir_losa
            tvMantenimientoLosa.text = if (losa.mantenimiento_losa) "Sí" else "No"
            tvPxHLosa.text = "S/ ${losa.precio_hora_losa}"

            val nombreDrawable = losa.ruta_imagen
                .substringAfterLast("/")
                .substringBeforeLast(".")

            val resId = context.resources.getIdentifier(nombreDrawable, "drawable", context.packageName)
            imageView.setImageResource(if (resId != 0) resId else R.drawable.losa_default)

            // Botón Reservar (si no está en mantenimiento)
            if (losa.mantenimiento_losa) {
                filaReservar.isEnabled = false
                filaReservar.alpha = 0.4f // visualmente grisado
            } else {
                filaReservar.isEnabled = true
                filaReservar.alpha = 1f
                filaReservar.setOnClickListener {
                    val intent = Intent(context, ReservarLosaActivity::class.java)
                    intent.putExtra("id_losa", losa.id_losa)
                    intent.putExtra("nombre_losa", losa.nombre_losa)
                    intent.putExtra("precio_hora_losa", losa.precio_hora_losa)
                    intent.putExtra("horario_losa", losa.horario_losa)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fila, parent, false)
        return MiViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val losa = listaLosas[position]
        holder.rellenarFila(losa)

        if (tipoUsuario == "admin") {
            holder.filaEditar.visibility = View.VISIBLE
            holder.filaEliminar.visibility = View.VISIBLE

            holder.filaEditar.setOnClickListener {
                val intent = Intent(context, FormularioActivity::class.java)
                intent.putExtra("id", losa.id_losa)
                intent.putExtra("nombre", losa.nombre_losa)
                intent.putExtra("descripcion", losa.descripcion_losa)
                intent.putExtra("horario", losa.horario_losa)
                intent.putExtra("direccion", losa.dir_losa)
                intent.putExtra("mantenimiento", losa.mantenimiento_losa)
                intent.putExtra("precio", losa.precio_hora_losa)
                intent.putExtra("imagen_id", losa.imagen_id)
                intent.putExtra("ruta_imagen", losa.ruta_imagen)
                context.startActivity(intent)
            }

            holder.filaEliminar.setOnClickListener {
                eliminarItem?.invoke(losa)
            }
        } else {
            holder.filaEditar.visibility = View.GONE
            holder.filaEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = listaLosas.size
}
