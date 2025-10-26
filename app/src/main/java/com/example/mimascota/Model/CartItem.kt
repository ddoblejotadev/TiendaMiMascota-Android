package com.example.mimascota.Model

data class CartItem(
    val producto: Producto,
    var cantidad: Int = 1
) {
    val subtotal: Int
        get() = producto.price * cantidad
}

