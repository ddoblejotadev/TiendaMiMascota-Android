package com.example.mimascota.repository

import android.content.Context
import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import com.example.mimascota.data.database.AppDatabase
import com.example.mimascota.data.entity.CartItemEntity

/**
 * CartRoomRepository: Intermedia entre CartViewModel y Room Database
 * Convierte entre CartItem (Model) y CartItemEntity (BD)
 */
class CartRoomRepository(context: Context) {

    private val cartItemDao = AppDatabase.getInstance(context).cartItemDao()

    suspend fun addToCart(cartItem: CartItem): Boolean {
        return try {
            val entity = CartItemEntity(
                productoId = cartItem.producto.id,
                nombre = cartItem.producto.name,
                precio = cartItem.producto.price,
                cantidad = cartItem.cantidad,
                imagen = cartItem.producto.imageUrl ?: ""
            )
            cartItemDao.insertCartItem(entity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCartItems(): List<CartItem> {
        return try {
            cartItemDao.getAllCartItems().map { it.toCartItem() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateCartItemQuantity(itemId: Int, newQuantity: Int): Boolean {
        return try {
            cartItemDao.getCartItemById(itemId)?.let { item ->
                cartItemDao.updateCartItem(item.copy(cantidad = newQuantity))
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removeFromCart(itemId: Int): Boolean {
        return try {
            cartItemDao.getCartItemById(itemId)?.let { item ->
                cartItemDao.deleteCartItem(item)
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun clearCart(): Boolean {
        return try {
            cartItemDao.clearCart()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun CartItemEntity.toCartItem() = CartItem(
        producto = Producto(
            id = productoId,
            name = nombre,
            description = null,
            price = precio,
            stock = 0,
            category = "",
            imageUrl = imagen
        ),
        cantidad = cantidad
    )
}

