package com.example.mimascota.repository

import android.content.Context
import com.example.mimascota.Model.User
import com.example.mimascota.data.database.AppDatabase
import com.example.mimascota.data.entity.UserEntity

/**
 * UserRepository: Intermedia entre ViewModel y la Base de Datos
 * Responsable de guardar, actualizar y recuperar datos de usuarios
 */
class UserRoomRepository(context: Context) {

    // Obtener instancia de la base de datos
    private val database = AppDatabase.getInstance(context)
    private val userDao = database.userDao()

    /**
     * Guardar un nuevo usuario en la BD
     */
    suspend fun saveUserToDatabase(user: User): Boolean {
        return try {
            val userEntity = UserEntity(
                email = user.email,
                username = user.username,
                password = user.password,
                direccion = user.direccion,
                run = user.run
            )
            userDao.insertUser(userEntity)
            true // Éxito
        } catch (e: Exception) {
            e.printStackTrace()
            false // Error
        }
    }

    /**
     * Buscar usuario por email y contraseña (para login)
     */
    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return try {
            val userEntity = userDao.getUserByEmailAndPassword(email, password)
            userEntity?.let {
                User(
                    id = it.id,
                    email = it.email,
                    username = it.username,
                    password = it.password,
                    direccion = it.direccion,
                    run = it.run
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Buscar usuario por email
     */
    suspend fun getUserByEmail(email: String): User? {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            userEntity?.let {
                User(
                    id = it.id,
                    email = it.email,
                    username = it.username,
                    password = it.password,
                    direccion = it.direccion,
                    run = it.run
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Actualizar foto de perfil de un usuario
     */
    suspend fun updateUserPhoto(userId: Int, photoPath: String): Boolean {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(fotoPerfil = photoPath)
                userDao.updateUser(updatedUser)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtener todos los usuarios
     */
    suspend fun getAllUsers(): List<User> {
        return try {
            userDao.getAllUsers().map { userEntity ->
                User(
                    id = userEntity.id,
                    email = userEntity.email,
                    username = userEntity.username,
                    password = userEntity.password,
                    direccion = userEntity.direccion,
                    run = userEntity.run
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Obtener foto de perfil de un usuario por ID
     */
    suspend fun getUserPhotoById(userId: Int): String? {
        return try {
            val user = userDao.getUserById(userId)
            user?.fotoPerfil
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

