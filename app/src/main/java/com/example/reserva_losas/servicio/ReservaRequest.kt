package com.example.reserva_losas.model

data class ReservaRequest(
    val cod_cli: Int,
    val id_losa: Int,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String
)
