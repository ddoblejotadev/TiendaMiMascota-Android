package com.example.mimascota.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mimascota.data.entity.CartItemEntity

/**
 * CartItemDao: Define operaciones SQL para la tabla carrito_items
 */
@Dao
interface CartItemDao {

    // INSERTAR: Agregar item al carrito
    @Insert
    suspend fun insertCartItem(item: CartItemEntity)

    // ACTUALIZAR: Cambiar cantidad de item
    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    // ELIMINAR: Remover item del carrito
    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    // CONSULTAR TODOS: Obtener todos los items del carrito
    @Query("SELECT * FROM carrito_items ORDER BY fechaAñadido DESC")
    suspend fun getAllCartItems(): List<CartItemEntity>

    // CONSULTAR POR ID: Buscar item específico
    @Query("SELECT * FROM carrito_items WHERE id = :itemId LIMIT 1")
    suspend fun getCartItemById(itemId: Int): CartItemEntity?

    // ELIMINAR TODOS: Vaciar carrito (después de compra exitosa)
    @Query("DELETE FROM carrito_items")
    suspend fun clearCart()

    // CONTAR ITEMS: Cuántos items hay en el carrito
    @Query("SELECT COUNT(*) FROM carrito_items")
    suspend fun getCartItemCount(): Int
}

