package com.example.mimascota.Model

data class CartItem(
    val producto: Producto,
    val cantidad: Int = 1
) {
    val subtotal: Int
        get() = producto.price * cantidad
}

