package com.example.reserva_losas.model

data class Losas(
    var id_losa:Int,
    var nombre_losa:String,
    var descripcion_losa:String,
    var horario_losa:String,
    var dir_losa:String,
    var mantenimiento_losa:Boolean,
    var precio_hora_losa:Double,
    val imagen_id: Int,
    val ruta_imagen: String
)
