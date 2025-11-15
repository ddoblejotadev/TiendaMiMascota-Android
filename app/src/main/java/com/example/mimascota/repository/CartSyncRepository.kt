package com.example.mimascota.repository

import com.example.mimascota.Model.CartItem
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.service.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CartSyncRepository: Repository para sincronizar el carrito con React Context
 *
 * Funcionalidades:
 * - Sincronización bidireccional con React
 * - Operaciones CRUD del carrito en el backend
 * - Manejo de errores de sincronización
 */
class CartSyncRepository {

    private val cartSyncService = RetrofitClient.cartSyncService

    /**
     * Resultado de operaciones de sincronización
     */
    sealed class SyncResult<out T> {
        data class Success<T>(val data: T) : SyncResult<T>()
        data class Error(val message: String) : SyncResult<Nothing>()
    }

    /**
     * Obtiene el carrito del usuario desde el backend (React Context)
     */
    suspend fun obtenerCarritoDesdeReact(userId: Int): SyncResult<List<CartItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cartSyncService.obtenerCarrito(userId)
                if (response.isSuccessful && response.body() != null) {
                    val carritoResponse = response.body()!!
                    val cartItems = carritoResponse.items.toCartItemList()
                    SyncResult.Success(cartItems)
                } else {
                    SyncResult.Error("Error al obtener carrito: ${response.message()}")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Sincroniza el carrito local con el backend (React Context)
     */
    suspend fun sincronizarCarritoConReact(
        userId: Int,
        items: List<CartItem>
    ): SyncResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val itemsDto = items.toDtoList()
                val request = SyncCarritoRequest(userId, itemsDto)
                val response = cartSyncService.sincronizarCarrito(request)

                if (response.isSuccessful && response.body() != null) {
                    val syncResponse = response.body()!!
                    if (syncResponse.success) {
                        SyncResult.Success(true)
                    } else {
                        SyncResult.Error(syncResponse.message)
                    }
                } else {
                    SyncResult.Error("Error al sincronizar: ${response.message()}")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Agrega un item al carrito en el backend
     */
    suspend fun agregarItemAlBackend(
        userId: Int,
        productoId: Int,
        cantidad: Int
    ): SyncResult<CarritoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = AgregarItemRequest(userId, productoId, cantidad)
                val response = cartSyncService.agregarItem(request)

                if (response.isSuccessful && response.body() != null) {
                    SyncResult.Success(response.body()!!)
                } else {
                    SyncResult.Error("Error al agregar item: ${response.message()}")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Elimina un item del carrito en el backend
     */
    suspend fun eliminarItemDelBackend(
        userId: Int,
        productoId: Int
    ): SyncResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cartSyncService.eliminarItem(userId, productoId)

                if (response.isSuccessful) {
                    SyncResult.Success(true)
                } else {
                    SyncResult.Error("Error al eliminar item: ${response.message()}")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Actualiza la cantidad de un item en el backend
     */
    suspend fun actualizarCantidadEnBackend(
        userId: Int,
        productoId: Int,
        cantidad: Int
    ): SyncResult<CarritoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ActualizarCantidadRequest(userId, productoId, cantidad)
                val response = cartSyncService.actualizarCantidad(request)

                if (response.isSuccessful && response.body() != null) {
                    SyncResult.Success(response.body()!!)
                } else {
                    SyncResult.Error("Error al actualizar cantidad: ${response.message()}")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Vacía el carrito en el backend
     */
    suspend fun vaciarCarritoEnBackend(userId: Int): SyncResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cartSyncService.vaciarCarrito(userId)

                if (response.isSuccessful) {
                    SyncResult.Success(true)
                } else {
                    SyncResult.Error("Error al vaciar carrito: ${response.message()}")
                }
            } catch (e: Exception) {
                SyncResult.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}

