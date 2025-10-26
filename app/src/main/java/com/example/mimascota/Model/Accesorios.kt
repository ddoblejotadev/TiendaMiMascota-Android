package com.example.mimascota.Model

data class Accesorios(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val tipoAccesorio: String,
    val material: String
) : Producto(idPro, namePro, descriptionPro, pricePro, stockPro, category = "Accesorios", imageUrl = "") {
}