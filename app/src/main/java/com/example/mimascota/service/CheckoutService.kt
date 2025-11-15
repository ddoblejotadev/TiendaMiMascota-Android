package com.example.mimascota.service

import com.example.mimascota.Model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * CheckoutService: API endpoints para checkout y órdenes
 */
interface CheckoutService {

    /**
     * Verifica si hay stock disponible para los productos en el carrito
     */
    @POST("productos/verificar-stock")
    suspend fun verificarStock(@Body request: VerificarStockRequest): Response<VerificarStockResponse>

    /**
     * Crea una nueva orden de compra
     */
    @POST("ordenes")
    suspend fun crearOrden(@Body request: CrearOrdenRequest): Response<OrdenResponse>

    /**
     * Obtiene el historial de órdenes del usuario
     */
    @GET("ordenes/usuario/{usuarioId}")
    suspend fun obtenerOrdenesUsuario(@Path("usuarioId") usuarioId: Int): Response<List<Orden>>

    /**
     * Obtiene los detalles de una orden específica
     */
    @GET("ordenes/{ordenId}")
    suspend fun obtenerDetalleOrden(@Path("ordenId") ordenId: Int): Response<Orden>

    /**
     * Cancela una orden (si está en estado PENDIENTE)
     */
    @PUT("ordenes/{ordenId}/cancelar")
    suspend fun cancelarOrden(@Path("ordenId") ordenId: Int): Response<Orden>
}

