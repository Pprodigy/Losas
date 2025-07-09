package com.example.reserva_losas.model

data class UsuarioResponse(
    val tipo: String,    // "cliente" o "admin"
    val id: Int,
    val nombre: String,
    val apellido: String
)

