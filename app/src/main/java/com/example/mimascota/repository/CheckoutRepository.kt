package com.example.mimascota.repository

import com.example.mimascota.Model.*
import com.example.mimascota.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CheckoutRepository: Capa de abstracción para operaciones de checkout y órdenes
 */
class CheckoutRepository {

    private val checkoutService = RetrofitClient.checkoutService

    /**
     * Resultado de operaciones
     */
    sealed class CheckoutResult<out T> {
        data class Success<T>(val data: T) : CheckoutResult<T>()
        data class Error(val message: String, val code: Int? = null) : CheckoutResult<Nothing>()
        object Loading : CheckoutResult<Nothing>()
    }

    /**
     * Verifica el stock disponible antes del checkout
     */
    suspend fun verificarStock(items: List<StockItem>): CheckoutResult<VerificarStockResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = VerificarStockRequest(items)
                val response = checkoutService.verificarStock(request)

                if (response.isSuccessful && response.body() != null) {
                    CheckoutResult.Success(response.body()!!)
                } else {
                    CheckoutResult.Error(
                        message = "Error al verificar stock: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                CheckoutResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Crea una nueva orden de compra
     */
    suspend fun crearOrden(
        usuarioId: Int,
        datosEnvio: DatosEnvio,
        items: List<OrdenItem>,
        total: Int,
        esInvitado: Boolean = false
    ): CheckoutResult<OrdenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CrearOrdenRequest(
                    usuarioId = usuarioId,
                    esInvitado = esInvitado,
                    datosEnvio = datosEnvio,
                    items = items,
                    total = total
                )

                val response = checkoutService.crearOrden(request)

                if (response.isSuccessful && response.body() != null) {
                    CheckoutResult.Success(response.body()!!)
                } else {
                    CheckoutResult.Error(
                        message = "Error al crear orden: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                CheckoutResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Obtiene el historial de órdenes del usuario
     */
    suspend fun obtenerOrdenesUsuario(usuarioId: Int): CheckoutResult<List<Orden>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = checkoutService.obtenerOrdenesUsuario(usuarioId)

                if (response.isSuccessful && response.body() != null) {
                    CheckoutResult.Success(response.body()!!)
                } else {
                    CheckoutResult.Error(
                        message = "Error al obtener órdenes: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                CheckoutResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Obtiene los detalles de una orden específica
     */
    suspend fun obtenerDetalleOrden(ordenId: Int): CheckoutResult<Orden> {
        return withContext(Dispatchers.IO) {
            try {
                val response = checkoutService.obtenerDetalleOrden(ordenId)

                if (response.isSuccessful && response.body() != null) {
                    CheckoutResult.Success(response.body()!!)
                } else {
                    CheckoutResult.Error(
                        message = "Orden no encontrada",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                CheckoutResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Cancela una orden
     */
    suspend fun cancelarOrden(ordenId: Int): CheckoutResult<Orden> {
        return withContext(Dispatchers.IO) {
            try {
                val response = checkoutService.cancelarOrden(ordenId)

                if (response.isSuccessful && response.body() != null) {
                    CheckoutResult.Success(response.body()!!)
                } else {
                    CheckoutResult.Error(
                        message = "No se pudo cancelar la orden: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                CheckoutResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }
}

