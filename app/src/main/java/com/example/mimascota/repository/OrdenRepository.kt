package com.example.mimascota.repository

import com.example.mimascota.model.Orden
import com.example.mimascota.config.ApiClient // cambio de paquete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * OrdenRepository: Repository para gestionar órdenes
 */
class OrdenRepository {
    private val apiService = ApiClient.apiService

    /**
     * Obtener todas las órdenes del usuario
     */
    suspend fun obtenerMisOrdenes(usuarioId: Int): Result<List<Orden>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerOrdenesUsuario(usuarioId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    /**
     * Obtener detalle de una orden
     */
    suspend fun obtenerDetalleOrden(ordenId: Long): Result<Orden> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerDetalleOrden(ordenId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    /**
     * Crear nueva orden
     */
    suspend fun crearOrden(orden: Orden): Result<Orden> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearOrden(orden)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    /**
     * Cancelar una orden
     */
    suspend fun cancelarOrden(ordenId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelarOrden(ordenId)

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
}
