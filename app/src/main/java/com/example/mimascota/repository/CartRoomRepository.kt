package com.example.mimascota.repository

import android.content.Context
import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import com.example.mimascota.data.database.AppDatabase
import com.example.mimascota.data.entity.CartItemEntity

/**
 * CartRoomRepository: Intermedia entre ViewModel del carrito y la Base de Datos
 * Responsable de guardar, actualizar y recuperar items del carrito
 */
class CartRoomRepository(context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val cartItemDao = database.cartItemDao()

    /**
     * Agregar item al carrito en la BD
     */
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

    /**
     * Obtener todos los items del carrito
     */
    suspend fun getCartItems(): List<CartItem> {
        return try {
            cartItemDao.getAllCartItems().map { entity ->
                val producto = Producto(
                    id = entity.productoId,
                    name = entity.nombre,
                    description = null,
                    price = entity.precio,
                    stock = 0,
                    category = "",
                    imageUrl = entity.imagen
                )
                CartItem(producto = producto, cantidad = entity.cantidad)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Actualizar cantidad de item en el carrito
     */
    suspend fun updateCartItemQuantity(itemId: Int, newQuantity: Int): Boolean {
        return try {
            val item = cartItemDao.getCartItemById(itemId)
            if (item != null) {
                val updatedItem = item.copy(cantidad = newQuantity)
                cartItemDao.updateCartItem(updatedItem)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Remover item del carrito
     */
    suspend fun removeFromCart(itemId: Int): Boolean {
        return try {
            val item = cartItemDao.getCartItemById(itemId)
            if (item != null) {
                cartItemDao.deleteCartItem(item)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Vaciar carrito completo
     */
    suspend fun clearCart(): Boolean {
        return try {
            cartItemDao.clearCart()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtener cantidad total de items en carrito
     */
    suspend fun getCartItemCount(): Int {
        return try {
            cartItemDao.getCartItemCount()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}

