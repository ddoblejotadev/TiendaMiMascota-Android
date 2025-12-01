package com.example.mimascota.repository

import android.util.Log
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository administrativo para operaciones de gestión (usuarios, órdenes)
 */
class AdminRepository {
    private val apiService = RetrofitClient.apiService
    private val TAG = "AdminRepository"

    sealed class AdminResult<out T> {
        data class Success<T>(val data: T) : AdminResult<T>()
        data class Error(val message: String) : AdminResult<Nothing>()
    }

    suspend fun getAllUsers(): AdminResult<List<Usuario>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching all users...")
                val response = apiService.getAllUsers()
                if (response.isSuccessful) {
                    Log.d(TAG, "Users fetched successfully.")
                    AdminResult.Success(response.body() ?: emptyList())
                } else {
                    Log.e(TAG, "Error fetching users: ${response.code()} - ${response.message()}")
                    AdminResult.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while fetching users: ${e.message}", e)
                AdminResult.Error("Error de conexión o endpoint no disponible")
            }
        }
    }

    suspend fun getAllOrders(): AdminResult<List<OrdenHistorial>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching all orders...")
                val response = apiService.getAllOrders()
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Orders fetched successfully.")
                    AdminResult.Success(response.body()!!.ordenes) // Corregido: acceder a la lista dentro del objeto de respuesta
                } else {
                    Log.e(TAG, "Error fetching orders: ${response.code()} - ${response.message()}")
                    AdminResult.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while fetching orders: ${e.message}", e)
                AdminResult.Error("Error de conexión o endpoint no disponible")
            }
        }
    }

    suspend fun updateOrderStatus(orderId: Long, status: String): AdminResult<OrdenHistorial> {
        return withContext(Dispatchers.IO) {
            try {
                val body = mapOf("estado" to status)
                val response = apiService.updateOrderStatus(orderId, body)
                if (response.isSuccessful && response.body() != null) {
                    AdminResult.Success(response.body()!!)
                } else {
                    AdminResult.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar estado: ${e.message}", e)
                AdminResult.Error("Error de conexión o endpoint no disponible")
            }
        }
    }

    suspend fun deleteUser(userId: Long): AdminResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteUser(userId)
                if (response.isSuccessful) {
                    AdminResult.Success(Unit)
                } else {
                    AdminResult.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar usuario: ${e.message}", e)
                AdminResult.Error("Error de conexión o endpoint no disponible")
            }
        }
    }
}
