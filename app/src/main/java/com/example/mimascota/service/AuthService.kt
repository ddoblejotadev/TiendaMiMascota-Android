package com.example.mimascota.service

import com.example.mimascota.Model.AuthResponse
import com.example.mimascota.Model.LoginRequest
import com.example.mimascota.Model.RegistroRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AuthService: Servicio de autenticación con el backend
 *
 * Endpoints:
 * - POST /auth/login - Iniciar sesión
 * - POST /auth/registro - Registrar nuevo usuario
 */
interface AuthService {

    /**
     * Login con email y contraseña
     * POST /auth/login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /**
     * Registro de nuevo usuario
     * POST /auth/registro
     */
    @POST("auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<AuthResponse>
}

