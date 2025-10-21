package com.example.mimascota.Model

data class Higiene(
    val idPro: String,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val tipoHigiene: String,
    val fragancia: String
) : Producto(idPro, namePro, descriptionPro, pricePro) {
}