package com.example.mimascota.model

/**
 * CartItem: Modelo que representa un producto en el carrito de compras
 */
data class CartItem(
    val producto: Producto,
    val cantidad: Int = 1
) {
    val subtotal: Double
        get() = producto.price * cantidad
}
