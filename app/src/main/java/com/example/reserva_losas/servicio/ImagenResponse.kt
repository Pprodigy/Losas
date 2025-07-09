package com.example.reserva_losas.servicio

import com.example.reserva_losas.model.Imagen
import com.google.gson.annotations.SerializedName

data class ImagenResponse(
    @SerializedName("listaImagenes") var listaImagenes: ArrayList<Imagen>
)
