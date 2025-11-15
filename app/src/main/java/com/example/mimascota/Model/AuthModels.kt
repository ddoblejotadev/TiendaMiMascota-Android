package com.example.mimascota.Model

import com.google.gson.annotations.SerializedName

/**
 * LoginRequest: DTO para enviar credenciales al backend
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

/**
 * RegistroRequest: DTO para registro de nuevo usuario
 */
data class RegistroRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("direccion")
    val direccion: String? = null,

    @SerializedName("run")
    val run: String? = null
)

/**
 * AuthResponse: DTO de respuesta del backend tras login/registro
 */
data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("usuario_id")
    val usuarioId: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("direccion")
    val direccion: String? = null,

    @SerializedName("run")
    val run: String? = null,

    @SerializedName("mensaje")
    val mensaje: String? = null
)

