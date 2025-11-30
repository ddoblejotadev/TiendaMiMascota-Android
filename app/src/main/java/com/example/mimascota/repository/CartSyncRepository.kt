package com.example.mimascota.repository

import com.example.mimascota.model.CartItem
import com.example.mimascota.service.CartSyncService
import com.example.mimascota.model.ApiResponse
import com.google.gson.Gson

class CartSyncRepository {

    private val cartSyncService: CartSyncService by lazy {
        CartSyncService()
    }

    /**
     * Sincroniza el carrito local con el backend (contexto React)
     */
    suspend fun sincronizarCarritoConReact(
        userId: Int,
        items: List<CartItem>
    ): SyncResult<Unit> {
        return try {
            val response = cartSyncService.sincronizarCarrito(userId, items)
            if (response.success) {
                SyncResult.Success(Unit) // Éxito
            } else {
                SyncResult.Error("Error en la respuesta del API: ${response.message}")
            }
        } catch (e: Exception) {
            SyncResult.Error("Error de red o servidor: ${e.message}")
        }
    }

    /**
     * Obtiene el carrito desde el backend (contexto React)
     */
    suspend fun obtenerCarritoDesdeReact(userId: Int): SyncResult<List<CartItem>> {
        return try {
            val response = cartSyncService.obtenerCarrito(userId)
            if (response.success && response.data != null) {
                SyncResult.Success(response.data)
            } else {
                SyncResult.Error("No se pudo obtener el carrito: ${response.message}")
            }
        } catch (e: Exception) {
            SyncResult.Error("Error de red o servidor: ${e.message}")
        }
    }

    /**
     * Representa el resultado de una operación de sincronización
     */
    sealed class SyncResult<T> {
        data class Success<T>(val data: T) : SyncResult<T>()
        data class Error<T>(val message: String) : SyncResult<T>()
    }
}
