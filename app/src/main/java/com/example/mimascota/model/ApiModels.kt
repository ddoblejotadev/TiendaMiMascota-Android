package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * LoginResponse: Respuesta del servidor al login
 */
data class LoginResponse(
    val token: String,
    val usuario: Usuario? = null,
    val mensaje: String? = null
)

/**
 * Orden: Modelo de orden de compra
 */
data class Orden(
    @SerializedName("orden_id")
    val ordenId: Long,
    val numeroOrden: String,
    val fecha: String,
    val fechaActualizacion: String? = null,
    val productos: List<ProductoOrden>,
    val datosEnvio: DatosEnvio,
    val subtotal: Int,
    val total: Int,
    val estado: String, // PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CANCELADO
    val usuarioId: Int
)

/**
 * ApiResponse: Respuesta genérica del API
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null
)

/**
 * ErrorResponse: Respuesta de error del API
 */
data class ErrorResponse(
    val message: String,
    val code: Int? = null,
    val errors: Map<String, List<String>>? = null
)
