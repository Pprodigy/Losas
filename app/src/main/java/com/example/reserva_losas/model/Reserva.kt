package com.example.reserva_losas.model

data class Reserva(
    val id_reserva: Int,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String,
    val estado: String,
    val nombre_losa: String,
    val dir_losa: String,
    val precio_hora_losa: Double
)

