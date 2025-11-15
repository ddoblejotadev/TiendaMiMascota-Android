package com.example.mimascota.service

import com.example.mimascota.Model.Producto
import retrofit2.Response
import retrofit2.http.*

/**
 * ProductoService: Interface que define los endpoints del API de productos
 *
 * Todos los métodos usan suspend functions para ser llamados desde coroutines
 * Los endpoints coinciden con el backend Spring Boot
 */
interface ProductoService {

    /**
     * Obtiene todos los productos
     * GET /productos
     */
    @GET("productos")
    suspend fun getAllProductos(): Response<List<Producto>>

    /**
     * Obtiene un producto por ID
     * GET /productos/{id}
     */
    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: Int): Response<Producto>

    /**
     * Obtiene productos por categoría
     * GET /productos/categoria/{categoria}
     */
    @GET("productos/categoria/{categoria}")
    suspend fun getProductosByCategoria(@Path("categoria") categoria: String): Response<List<Producto>>

    /**
     * Crea un nuevo producto
     * POST /productos
     */
    @POST("productos")
    suspend fun createProducto(@Body producto: Producto): Response<Producto>

    /**
     * Actualiza un producto existente
     * PUT /productos/{id}
     */
    @PUT("productos/{id}")
    suspend fun updateProducto(
        @Path("id") id: Int,
        @Body producto: Producto
    ): Response<Producto>

    /**
     * Elimina un producto
     * DELETE /productos/{id}
     */
    @DELETE("productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Int): Response<Unit>
}

