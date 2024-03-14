package com.svs.ista.model

import com.google.gson.annotations.SerializedName

data class Actividades(
    val id_actividad: Int,
    val descripcion: String,
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val visible: Boolean,
    val usuario: Usuar
)
data class Usuar(
    @SerializedName("id")
    val id: Int,
    val username: String,
    val password: String
)
