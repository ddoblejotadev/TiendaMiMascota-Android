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

    @PUT("auth/usuario")
    suspend fun updateCurrentUser(@Body usuario: Usuario): Response<Usuario>

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

    @POST("ordenes/verificar-stock")
    suspend fun verificarStock(
        @Body request: VerificarStockRequest
    ): Response<VerificarStockResponse>

    @POST("ordenes")
    suspend fun crearOrden(
        @Body request: CrearOrdenRequest
    ): Response<OrdenResponse>

    @GET("ordenes/usuario/{id}")
    suspend fun obtenerOrdenesUsuario(
        @Path("id") usuarioId: Long
    ): Response<List<OrdenHistorial>>

    @GET("ordenes/{id}")
    suspend fun obtenerDetalleOrden(
        @Path("id") ordenId: Long
    ): Response<OrdenHistorial>

    @DELETE("ordenes/{id}")
    suspend fun cancelarOrden(
        @Path("id") ordenId: Long
    ): Response<OrdenHistorial>

    // Algunos backends usan la ruta plural `ordenes/{id}` para obtener órdenes por usuario.
    // Añadimos una alternativa para maximizar compatibilidad.
    @GET("ordenes/{id}")
    suspend fun obtenerOrdenesUsuarioAlt(
        @Path("id") usuarioId: Long
    ): Response<List<OrdenHistorial>>

    // ============= ADMIN / USUARIOS (opcional, solo si backend soporta) =============

    @GET("admin/usuarios")
    suspend fun getAllUsers(): Response<List<Usuario>>

    @GET("admin/usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<Usuario>

    @PUT("admin/usuarios/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body usuario: Usuario): Response<Usuario>

    @DELETE("admin/usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    // ============= ADMIN ÓRDENES (opcional) =============

    @GET("ordenes")
    suspend fun getAllOrders(): Response<List<OrdenHistorial>>

    @PUT("admin/ordenes/{id}")
    suspend fun updateOrderStatus(@Path("id") id: Long, @Body body: Map<String, String>): Response<OrdenHistorial>

}