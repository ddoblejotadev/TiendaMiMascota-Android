package com.example.mimascota.Model

data class Higiene(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val tipoHigiene: String,
    val fragancia: String
) : Producto(idPro, namePro, descriptionPro, pricePro, stockPro, category = "Higiene", imageUrl = "") {
}