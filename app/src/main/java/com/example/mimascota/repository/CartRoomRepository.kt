package com.example.mimascota.repository

import com.example.mimascota.data.dao.CartItemDao
import com.example.mimascota.data.entity.CartItemEntity
import com.example.mimascota.model.CartItem
import com.example.mimascota.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRoomRepository(private val cartItemDao: CartItemDao) {

    val allItems: Flow<List<CartItem>> = cartItemDao.getAllCartItems().map { entities ->
        entities.map { entity -> entityToCartItem(entity) }
    }

    private fun entityToCartItem(entity: CartItemEntity): CartItem {
        val producto = Producto(
            producto_id = entity.productoId,
            producto_nombre = entity.nombre,
            price = entity.precio,
            imageUrl = entity.imagen
            // Nota: otros campos opcionales se dejan nulos
        )
        return CartItem(producto, entity.cantidad)
    }

    private fun cartItemToEntity(cartItem: CartItem): CartItemEntity {
        return CartItemEntity(
            id = cartItem.producto.producto_id, // Usar ID del producto como PK para la entidad del carrito
            productoId = cartItem.producto.producto_id,
            nombre = cartItem.producto.producto_nombre,
            precio = cartItem.producto.price,
            cantidad = cartItem.cantidad,
            imagen = cartItem.producto.imageUrl ?: ""
        )
    }

    suspend fun insert(cartItem: CartItem) {
        cartItemDao.insertCartItem(cartItemToEntity(cartItem))
    }

    suspend fun update(cartItem: CartItem) {
        cartItemDao.updateCartItem(cartItemToEntity(cartItem))
    }

    suspend fun delete(cartItem: CartItem) {
        cartItemDao.deleteCartItem(cartItemToEntity(cartItem))
    }

    suspend fun clearCart() {
        cartItemDao.clearCart()
    }

    suspend fun getItemById(productId: Int): CartItem? {
        return cartItemDao.getCartItemById(productId)?.let { entityToCartItem(it) }
    }
}
