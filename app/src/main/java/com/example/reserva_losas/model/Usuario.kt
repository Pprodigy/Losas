package com.example.reserva_losas.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val correo: String,
    val celular: String,
    val tipo: String // "cliente" o "admin"
)