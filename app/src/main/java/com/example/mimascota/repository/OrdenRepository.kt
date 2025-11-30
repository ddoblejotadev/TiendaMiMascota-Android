package com.example.mimascota.repository

import com.example.mimascota.model.CrearOrdenRequest
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.model.ItemOrden
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.OrdenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * OrdenRepository: Repository para gestionar órdenes
 */
class OrdenRepository {
    // Corregido: Usar la instancia única de apiService desde RetrofitClient
    private val apiService = RetrofitClient.apiService

    /**
     * Obtener todas las órdenes del usuario
     */
    suspend fun obtenerMisOrdenes(usuarioId: Long): Result<List<OrdenHistorial>> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("OrdenRepository", "Solicitando órdenes para usuarioId=$usuarioId")
                val response = apiService.obtenerOrdenesUsuario(usuarioId)
                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    android.util.Log.d("OrdenRepository", "Órdenes recibidas: count=${body.size}")
                    Result.success(body)
                } else if (response.code() == 404) {
                    // Backend puede devolver 404 si no hay órdenes; tratar como lista vacía
                    android.util.Log.i("OrdenRepository", "No se encontraron órdenes para userId=$usuarioId (404). Retornando lista vacía.")
                    Result.success(emptyList())
                } else {
                    val bodyStr = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    android.util.Log.w("OrdenRepository", "Error al obtener órdenes userId=$usuarioId code=${response.code()} body=$bodyStr")
                    Result.failure(Exception("Error ${response.code()}: ${response.message()} Body=$bodyStr"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    /**
     * Obtener detalle de una orden
     */
    suspend fun obtenerDetalleOrden(ordenId: Long): Result<OrdenHistorial> {
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
    suspend fun crearOrden(request: CrearOrdenRequest): Result<OrdenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearOrden(request)

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
    suspend fun cancelarOrden(ordenId: Long): Result<OrdenHistorial> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelarOrden(ordenId)

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
}
