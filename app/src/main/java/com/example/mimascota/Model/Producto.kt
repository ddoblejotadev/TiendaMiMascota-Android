package com.example.mimascota.Model

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("producto_id")
    val id: Int,
    @SerializedName("producto_nombre")
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val imageUrl: String

) {
}