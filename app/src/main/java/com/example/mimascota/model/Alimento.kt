package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

class Alimento(
    // Propiedades de la clase base Producto
    @SerializedName("producto_id")
    override val producto_id: Int,
    @SerializedName("producto_nombre")
    override val producto_nombre: String,
    @SerializedName("price")
    // Cambiado a Double
    override val price: Double,
    @SerializedName("category")
    override val category: String = "Alimento",
    @SerializedName("description")
    override val description: String?,
    @SerializedName("imageUrl")
    override val imageUrl: String?,
    @SerializedName("stock")
    override val stock: Int?,

    // Propiedades específicas de Alimento
    @SerializedName("marca")
    override val marca: String?,
    @SerializedName("tipo")
    override val tipo: String?,
    @SerializedName("peso")
    override val peso: Double?
) : Producto(
    producto_id = producto_id,
    producto_nombre = producto_nombre,
    price = price,
    category = category,
    description = description,
    imageUrl = imageUrl,
    stock = stock
)
