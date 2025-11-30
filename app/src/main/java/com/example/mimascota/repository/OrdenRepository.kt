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

                // 1) Intentar endpoint canonico /orden/usuario/{id}
                try {
                    val response = apiService.obtenerOrdenesUsuario(usuarioId)
                    if (response.isSuccessful) {
                        val body = response.body() ?: emptyList()
                        android.util.Log.d("OrdenRepository", "Órdenes recibidas (orden/usuario): count=${body.size}")
                        return@withContext Result.success(body)
                    } else if (response.code() != 404) {
                        val bodyStr = try { response.errorBody()?.string() } catch (_: Exception) { null }
                        android.util.Log.w("OrdenRepository", "orden/usuario returned ${response.code()} body=$bodyStr")
                        return@withContext Result.failure(Exception("Error ${response.code()}: ${response.message()} Body=$bodyStr"))
                    }
                } catch (ex: Exception) {
                    android.util.Log.w("OrdenRepository", "Error llamando orden/usuario: ${ex.message}")
                }

                // 2) Intentar endpoint alternativo /ordenes/{id}
                try {
                    val altResp = apiService.obtenerOrdenesUsuarioAlt(usuarioId)
                    if (altResp.isSuccessful) {
                        val body = altResp.body() ?: emptyList()
                        android.util.Log.d("OrdenRepository", "Órdenes recibidas (ordenes/{id}): count=${body.size}")
                        return@withContext Result.success(body)
                    } else if (altResp.code() != 404) {
                        val bodyStr = try { altResp.errorBody()?.string() } catch (_: Exception) { null }
                        android.util.Log.w("OrdenRepository", "ordenes/{id} returned ${altResp.code()} body=$bodyStr")
                        return@withContext Result.failure(Exception("Error ${altResp.code()}: ${altResp.message()} Body=$bodyStr"))
                    }
                } catch (ex: Exception) {
                    android.util.Log.w("OrdenRepository", "Error llamando ordenes/{id}: ${ex.message}")
                }

                // 3) Fallback: intentar endpoint admin/ordenes y filtrar por usuarioId
                try {
                    android.util.Log.i("OrdenRepository", "Endpoint específico no disponible (404). Intentando fallback /admin/ordenes.")
                    val adminResp = apiService.getAllOrders()
                    if (adminResp.isSuccessful) {
                        val all = adminResp.body() ?: emptyList()
                        val filtered = all.filter { it.usuarioId == usuarioId }
                        android.util.Log.i("OrdenRepository", "Fallback: órdenes totales=${all.size}, filtradas=${filtered.size}")
                        return@withContext Result.success(filtered)
                    } else {
                        android.util.Log.w("OrdenRepository", "Fallback admin/ordenes devolvió code=${adminResp.code()}")
                        return@withContext Result.success(emptyList())
                    }
                } catch (ex: Exception) {
                    android.util.Log.w("OrdenRepository", "Error en fallback admin/ordenes: ${ex.message}")
                    return@withContext Result.success(emptyList())
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
