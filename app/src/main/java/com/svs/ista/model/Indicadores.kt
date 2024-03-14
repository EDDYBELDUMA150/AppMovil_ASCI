package com.svs.ista.model

data class Indicadores(
    val id_indicador: Int,
    val nombre: String,
    val peso: Double,
    val estandar: Double,
    val porc_utilida_obtenida: Double
)
