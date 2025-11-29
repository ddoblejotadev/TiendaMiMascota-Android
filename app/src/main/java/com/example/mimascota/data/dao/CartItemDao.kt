package com.example.mimascota.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mimascota.data.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): Define operaciones SQL para la tabla carrito_items
 * Room genera automáticamente las consultas SQL
 */
@Dao
interface CartItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("SELECT * FROM carrito_items ORDER BY fechaAñadido DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM carrito_items WHERE productoId = :productId LIMIT 1")
    suspend fun getCartItemById(productId: Int): CartItemEntity?

    @Query("DELETE FROM carrito_items")
    suspend fun clearCart()

    @Query("SELECT COUNT(*) FROM carrito_items")
    suspend fun getCartItemCount(): Int
}
