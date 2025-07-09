package com.example.reserva_losas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reserva_losas.R
import com.example.reserva_losas.model.Reserva

class ReservaAdapter(
    private val reservas: List<Reserva>,
    private val onCancelarClick: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLosa: TextView = itemView.findViewById(R.id.tvLosa)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvHoras: TextView = itemView.findViewById(R.id.tvHoras)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val btnCancelar: Button = itemView.findViewById(R.id.btnCancelar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]

        holder.tvLosa.text = "Losa: ${reserva.nombre_losa}"
        holder.tvFecha.text = "Fecha: ${reserva.fecha}"
        holder.tvHoras.text = "Hora: ${reserva.hora_inicio} - ${reserva.hora_fin}"
        holder.tvEstado.text = "Estado: ${reserva.estado}"

        if (reserva.estado == "Pendiente" || reserva.estado == "Confirmada") {
            holder.btnCancelar.visibility = View.VISIBLE
            holder.btnCancelar.setOnClickListener {
                onCancelarClick(reserva)
            }
        } else {
            holder.btnCancelar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = reservas.size
}
