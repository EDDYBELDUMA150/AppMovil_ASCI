package com.svs.ista.model

import com.google.gson.annotations.SerializedName

data class Persona(
    @SerializedName("id_persona") val id: Int,
    @SerializedName("cedula") val cedula: String,
    @SerializedName("primer_nombre") val primerNombre: String,
    @SerializedName("segundo_nombre") val segundoNombre: String,
    @SerializedName("primer_apellido") val primerApellido: String,
    @SerializedName("segundo_apellido") val segundoApellido: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("celular") val celular: String,
    @SerializedName("visible") val visible: Boolean
)
