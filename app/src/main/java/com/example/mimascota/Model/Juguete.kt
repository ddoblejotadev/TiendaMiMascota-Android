package com.example.mimascota.Model

data class Juguete(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val material: String,
    val tamano: String
) : Producto(idPro, namePro, descriptionPro, pricePro, stockPro, category = "Juguete", imageUrl = "") {
}