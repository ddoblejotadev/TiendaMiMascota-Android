package com.example.mimascota.Model

/**
 * User: Modelo de usuario para la UI
 *
 * Este modelo se usa en ViewModels y Views.
 * Es diferente de UserEntity (que es para la BD Room)
 */
data class User(
    val id: Int,
    val run: String,
    val username: String,
    val email: String,
    val password: String,
    val direccion: String
)
