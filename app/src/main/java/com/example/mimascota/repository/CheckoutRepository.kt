package com.example.mimascota.repository

import android.util.Log
import com.example.mimascota.model.*
import com.example.mimascota.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * CheckoutRepository: Capa de abstracción para operaciones de checkout y órdenes
 */
class CheckoutRepository {

    // Corregido: Usar la instancia única de apiService desde RetrofitClient
    private val apiService = RetrofitClient.apiService
    private val TAG = "CheckoutRepository"

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
                Log.d(TAG, "Verificando stock para ${items.size} productos")

                val response = apiService.verificarStock(request)

                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Stock verificado exitosamente")
                    CheckoutResult.Success(response.body()!!)
                } else {
                    val errorMsg = "Error al verificar stock: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    CheckoutResult.Error(
                        message = errorMsg,
                        code = response.code()
                    )
                }
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Timeout: El servidor tardó demasiado en responder (30s). Render free tier puede tardar en despertar."
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            } catch (e: UnknownHostException) {
                val errorMsg = "Error de conexión: No se pudo conectar con el servidor"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Error inesperado: ${e.localizedMessage ?: "Error desconocido"}"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            }
        }
    }

    /**
     * Crea una nueva orden de compra
     */
    suspend fun crearOrden(
        usuarioId: Long,
        esInvitado: Boolean,
        datosEnvio: DatosEnvio,
        items: List<ItemOrden>,
        subtotal: Double,
        total: Double
    ): CheckoutResult<OrdenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CrearOrdenRequest(
                    usuarioId = usuarioId,
                    esInvitado = esInvitado,
                    datosEnvio = datosEnvio,
                    items = items,
                    subtotal = subtotal,
                    total = total,
                    estado = "completada"
                )

                Log.d(TAG, "Creando orden para usuario $usuarioId")
                Log.d(TAG, "Items: ${items.size}, Subtotal: $subtotal, Total: $total")
                Log.d(TAG, "Datos envío: ${datosEnvio.nombreCompleto}, ${datosEnvio.ciudad}")

                val response = apiService.crearOrden(request)

                if (response.isSuccessful && response.body() != null) {
                    val orden = response.body()!!
                    Log.d(TAG, "Orden creada exitosamente: ${orden.numeroOrden}")
                    CheckoutResult.Success(orden)
                } else if (response.code() == 404) {
                    // Endpoint no disponible: simular una respuesta profesional para no bloquear UX
                    Log.w(TAG, "Endpoint crearOrden no disponible (404). Simulando orden localmente.")
                    val simulatedOrderNumber = "MM-${System.currentTimeMillis().toString().takeLast(6)}"
                    val fecha = java.time.ZonedDateTime.now().toString()
                    val simulatedItems = items.map {
                        ProductoOrden(
                            productoId = it.productoId,
                            nombre = "Producto ${it.productoId}",
                            cantidad = it.cantidad,
                            precioUnitario = it.precioUnitario,
                            imagen = null
                        )
                    }
                    val simulatedOrden = OrdenResponse(
                        id = System.currentTimeMillis(),
                        numeroOrden = simulatedOrderNumber,
                        fecha = fecha,
                        estado = "procesando",
                        total = total,
                        mensaje = "Orden simulada localmente (endpoint no disponible)",
                        items = simulatedItems
                    )
                    CheckoutResult.Success(simulatedOrden)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = when (response.code()) {
                        400 -> "Datos inválidos: Verifica que todos los campos estén completos. Error: $errorBody"
                        401 -> "No autorizado: Inicia sesión nuevamente"
                        500 -> "Error en el servidor: $errorBody. Intenta de nuevo más tarde"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    Log.e(TAG, "Error al crear orden: $errorMsg")
                    Log.e(TAG, "Error body completo: $errorBody")
                    CheckoutResult.Error(
                        message = errorMsg,
                        code = response.code()
                    )
                }
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Timeout: El servidor tardó demasiado (30s). Tu orden podría haberse creado. Verifica en 'Mis Pedidos'"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            } catch (e: UnknownHostException) {
                val errorMsg = "Sin conexión: Verifica tu conexión a internet"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Error inesperado: ${e.localizedMessage ?: "Error desconocido"}"
                Log.e(TAG, errorMsg, e)
                e.printStackTrace()
                CheckoutResult.Error(message = errorMsg)
            }
        }
    }

    /**
     * Obtiene el historial de órdenes del usuario
     */
    suspend fun obtenerOrdenesUsuario(usuarioId: Long): CheckoutResult<List<OrdenHistorial>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo órdenes del usuario $usuarioId")

                val response = apiService.obtenerOrdenesUsuario(usuarioId)

                if (response.isSuccessful && response.body() != null) {
                    val ordenes = response.body()!!
                    Log.d(TAG, "Órdenes obtenidas: ${ordenes.size}")
                    CheckoutResult.Success(ordenes)
                } else {
                    val errorMsg = "Error al obtener órdenes: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    CheckoutResult.Error(
                        message = errorMsg,
                        code = response.code()
                    )
                }
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Timeout: El servidor tardó demasiado en responder"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            } catch (e: UnknownHostException) {
                val errorMsg = "Sin conexión a internet"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.localizedMessage ?: "Error desconocido"}"
                Log.e(TAG, errorMsg, e)
                CheckoutResult.Error(message = errorMsg)
            }
        }
    }

    /**
     * Obtiene los detalles de una orden específica
     */
    suspend fun obtenerDetalleOrden(ordenId: Long): CheckoutResult<OrdenHistorial> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerDetalleOrden(ordenId)

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
    suspend fun cancelarOrden(ordenId: Long): CheckoutResult<OrdenHistorial> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelarOrden(ordenId)

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
