package com.example.mimascota.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mimascota.data.entity.UserEntity

/**
 * DAO (Data Access Object): Define operaciones SQL para la tabla usuarios
 * Room genera automáticamente las consultas SQL
 */
@Dao
interface UserDao {

    // INSERTAR: Guardar un nuevo usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // ACTUALIZAR: Modificar un usuario existente
    @Update
    suspend fun updateUser(user: UserEntity)

    // ELIMINAR: Borrar un usuario
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // CONSULTAR POR EMAIL: Buscar usuario por email
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // CONSULTAR POR ID: Buscar usuario por ID
    @Query("SELECT * FROM usuarios WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    // CONSULTAR TODOS: Obtener todos los usuarios
    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsers(): List<UserEntity>

    // VERIFICAR CREDENCIALES: Buscar usuario por email y contraseña
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    // CONTAR USUARIOS: Útil para verificar si BD está vacía
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getUserCount(): Int
}

