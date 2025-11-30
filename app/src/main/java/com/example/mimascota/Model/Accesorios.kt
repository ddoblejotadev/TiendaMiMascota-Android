package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

class Accesorios(
    // Propiedades de la clase base Producto
    @SerializedName("producto_id")
    override val producto_id: Int,
    @SerializedName("producto_nombre")
    override val producto_nombre: String,
    @SerializedName("price")
    override val price: Double,
    @SerializedName("category")
    override val category: String = "Accesorios",
    @SerializedName("description")
    override val description: String?,
    @SerializedName("imageUrl")
    override val imageUrl: String?,
    @SerializedName("stock")
    override val stock: Int?,

    // Propiedades específicas de Accesorios
    @SerializedName("tipoAccesorio")
    override val tipoAccesorio: String?,
    @SerializedName("material")
    override val material: String?
) : Producto(
    producto_id = producto_id,
    producto_nombre = producto_nombre,
    price = price,
    category = category,
    description = description,
    imageUrl = imageUrl,
    stock = stock
)
