package com.example.reserva_losas.servicio

import com.example.reserva_losas.model.Losas
import com.google.gson.annotations.SerializedName

data class LosasResponse(
    @SerializedName("listaLosas") var listaLosas:ArrayList<Losas>
)
