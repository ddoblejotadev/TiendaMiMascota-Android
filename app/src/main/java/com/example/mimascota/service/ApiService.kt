package com.example.mimascota.service

import com.example.mimascota.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * ApiService: Interface Retrofit con todos los endpoints del API
 */
interface ApiService {

    // ============= AUTENTICACIÓN =============

    /**
     * Login con email y password
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Registro de nuevo usuario
     */
    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<LoginResponse>

    /**
     * Verificar token válido
     */
    @GET("auth/verificar")
    suspend fun verificarToken(): Response<Usuario>

    /**
     * Obtener datos del usuario actual
     */
    @GET("auth/usuario")
    suspend fun obtenerUsuario(): Response<Usuario>

    /**
     * Logout
     */
    @POST("auth/logout")
    suspend fun logout(): Response<Void>

    // ============= ÓRDENES =============

    /**
     * Obtener todas las órdenes del usuario
     */
    @GET("ordenes/usuario/{usuarioId}")
    suspend fun obtenerOrdenesUsuario(
        @Path("usuarioId") usuarioId: Int
    ): Response<List<Orden>>

    /**
     * Obtener detalle de una orden específica
     */
    @GET("ordenes/{ordenId}")
    suspend fun obtenerDetalleOrden(
        @Path("ordenId") ordenId: Long
    ): Response<Orden>

    /**
     * Crear nueva orden
     */
    @POST("ordenes")
    suspend fun crearOrden(@Body orden: Orden): Response<Orden>

    /**
     * Actualizar estado de una orden
     */
    @PUT("ordenes/{ordenId}/estado")
    suspend fun actualizarEstadoOrden(
        @Path("ordenId") ordenId: Long,
        @Query("estado") estado: String
    ): Response<Orden>

    /**
     * Cancelar una orden
     */
    @DELETE("ordenes/{ordenId}")
    suspend fun cancelarOrden(
        @Path("ordenId") ordenId: Long
    ): Response<Void>

    // ============= PRODUCTOS =============

    /**
     * Obtener todos los productos
     */
    @GET("productos")
    suspend fun obtenerProductos(
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 20
    ): Response<List<Producto>>

    /**
     * Obtener productos por categoría
     */
    @GET("productos/categoria/{categoria}")
    suspend fun obtenerProductosPorCategoria(
        @Path("categoria") categoria: String
    ): Response<List<Producto>>

    /**
     * Obtener detalle de un producto
     */
    @GET("productos/{productoId}")
    suspend fun obtenerProducto(
        @Path("productoId") productoId: Int
    ): Response<Producto>

    /**
     * Buscar productos
     */
    @GET("productos/buscar")
    suspend fun buscarProductos(
        @Query("q") query: String
    ): Response<List<Producto>>
}


