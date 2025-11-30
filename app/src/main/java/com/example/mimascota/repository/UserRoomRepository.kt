package com.example.mimascota.repository

import android.content.Context
import com.example.mimascota.model.User
import com.example.mimascota.data.database.AppDatabase
import com.example.mimascota.data.entity.UserEntity

/**
 * UserRoomRepository: Intermedia entre ViewModel y Room Database
 * Convierte entre User (Model) y UserEntity (BD)
 */
class UserRoomRepository(context: Context) {

    private val userDao = AppDatabase.getInstance(context).userDao()

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
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return try {
            userDao.getUserByEmailAndPassword(email, password)?.toUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Suppress("unused")
    suspend fun getUserByEmail(email: String): User? {
        return try {
            userDao.getUserByEmail(email)?.toUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUserPhoto(userId: Int, photoPath: String): Boolean {
        return try {
            val user = userDao.getUserById(userId) ?: return false
            userDao.updateUser(user.copy(fotoPerfil = photoPath))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Suppress("unused")
    suspend fun getAllUsers(): List<User> {
        return try {
            userDao.getAllUsers().map { it.toUser() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserPhotoById(userId: Int): String? {
        return try {
            userDao.getUserById(userId)?.fotoPerfil
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun UserEntity.toUser() = User(
        id = id,
        email = email,
        username = username,
        password = password,
        direccion = direccion,
        run = run
    )
}


