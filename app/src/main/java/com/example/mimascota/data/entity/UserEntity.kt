package com.example.mimascota.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity: Representa la tabla "usuarios" en la base de datos SQLite
 * Room automáticamente crea la tabla basada en esta clase
 */
@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val username: String,
    val password: String,
    val direccion: String,
    val run: String,
    val fotoPerfil: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis()
)

