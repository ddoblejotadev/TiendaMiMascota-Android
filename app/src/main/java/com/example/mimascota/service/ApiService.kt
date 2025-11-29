package com.example.mimascota.service

import com.example.mimascota.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * ApiService: Interfaz ÚNICA de Retrofit con todos los endpoints del API
 */
interface ApiService {

    // ============= AUTENTICACIÓN =============

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<LoginResponse>

    @GET("auth/verificar")
    suspend fun verificarToken(): Response<Usuario>

    @GET("auth/usuario")
    suspend fun obtenerUsuario(): Response<Usuario>

    @POST("auth/logout")
    suspend fun logout(): Response<Void>

    // ============= PRODUCTOS =============

    @GET("productos")
    suspend fun getAllProductos(
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 50 // Aumentado el límite para traer más productos
    ): Response<List<Producto>>

    @GET("productos/{productoId}")
    suspend fun getProductoById(
        @Path("productoId") productoId: Int
    ): Response<Producto>

    @POST("productos")
    suspend fun createProducto(
        @Body producto: Producto, 
        @Query("tipo") tipoProducto: String
    ): Response<Producto>

    @PUT("productos/{id}")
    suspend fun updateProducto(
        @Path("id") id: Int, 
        @Body producto: Producto
    ): Response<Producto>

    @DELETE("productos/{id}")
    suspend fun deleteProducto(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("productos/categoria/{categoria}")
    suspend fun getProductosByCategoria(
        @Path("categoria") categoria: String
    ): Response<List<Producto>>

    @GET("productos/buscar")
    suspend fun searchProductos(
        @Query("q") query: String
    ): Response<List<Producto>>

    // ============= ÓRDENES (CHECKOUT) =============

    @POST("orden/verificar-stock")
    suspend fun verificarStock(
        @Body request: VerificarStockRequest
    ): Response<VerificarStockResponse>

    @POST("orden/crear")
    suspend fun crearOrden(
        @Body request: CrearOrdenRequest
    ): Response<OrdenResponse>

    @GET("orden/usuario/{id}")
    suspend fun obtenerOrdenesUsuario(
        @Path("id") usuarioId: Long
    ): Response<List<OrdenHistorial>>

    @GET("orden/detalle/{id}")
    suspend fun obtenerDetalleOrden(
        @Path("id") ordenId: Long
    ): Response<OrdenHistorial>

    @POST("orden/cancelar/{id}")
    suspend fun cancelarOrden(
        @Path("id") ordenId: Long
    ): Response<OrdenHistorial>
}
