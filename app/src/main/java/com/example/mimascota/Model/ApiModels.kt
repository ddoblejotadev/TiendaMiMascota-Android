package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * Usuario: Modelo de usuario del sistema
 */
data class Usuario(
    @SerializedName("usuario_id")
    val usuarioId: Int,
    val nombre: String,
    val email: String,
    val telefono: String? = null,
    val rol: String = "USUARIO"
)

/**
 * LoginRequest: Datos para login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * LoginResponse: Respuesta del servidor al login
 */
data class LoginResponse(
    val token: String,
    val usuario: Usuario,
    val mensaje: String? = null
)

/**
 * RegistroRequest: Datos para registro
 */
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String? = null
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
 * ProductoOrden: Producto dentro de una orden
 */
data class ProductoOrden(
    @SerializedName("producto_id")
    val productoId: Int,
    @SerializedName("producto_nombre")
    val nombre: String,
    val precio: Int,
    val cantidad: Int,
    val imagen: String? = null,
    val subtotal: Int
)

/**
 * DatosEnvio: Información de envío
 */
data class DatosEnvio(
    val nombreCompleto: String,
    val direccion: String,
    val ciudad: String,
    val region: String,
    val telefono: String,
    val codigoPostal: String? = null,
    val email: String? = null
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

/**
 * Producto: Modelo de producto (desde ApiService)
 */
data class Producto(
    @SerializedName("producto_id")
    val productoId: Int,
    @SerializedName("producto_nombre")
    val nombreProducto: String,
    val descripcion: String?,
    val precio: Int,
    val stock: Int,
    val categoria: String,
    val imagen: String?,
    val activo: Boolean = true
)

