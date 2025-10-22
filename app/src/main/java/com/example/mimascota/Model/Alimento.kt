package com.example.mimascota.Model

data class Alimento(
    val idPro: Int,
    val namePro: String,
    val descriptionPro: String,
    val pricePro: Int,
    val stockPro: Int,
    val marca: String,
    val tipo: String,
    val peso: Double
) : Producto(idPro, namePro, descriptionPro, pricePro, stockPro, category = "Alimento", imageUrl = "") {

}