package com.example.mimascota.service

import retrofit2.Response
import retrofit2.http.GET

/**
 * HealthService: Servicio para verificar la conexión con el backend
 */
interface HealthService {

    /**
     * Endpoint de salud del backend
     * Verifica que el servidor esté activo
     */
    @GET("health")
    suspend fun checkHealth(): Response<Unit>

    /**
     * Alternativa: ping genérico
     */
    @GET("productos")
    suspend fun pingProductos(): Response<Any>
}

