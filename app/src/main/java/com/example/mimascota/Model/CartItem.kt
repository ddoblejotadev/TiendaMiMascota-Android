package com.example.mimascota.Model

/**
 * CartItem: Modelo que representa un producto en el carrito de compras
 *
 * Contiene el producto y su cantidad, además calcula automáticamente
 * el subtotal (precio × cantidad)
 */
data class CartItem(
    val producto: Producto,
    val cantidad: Int = 1
) {
    val subtotal: Int
        get() = producto.price * cantidad
}

