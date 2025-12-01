package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val _users = MutableStateFlow<List<Usuario>>(emptyList())
    val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private val _orders = MutableStateFlow<List<OrdenHistorial>>(emptyList())
    val orders: StateFlow<List<OrdenHistorial>> = _orders.asStateFlow()

    private val apiService = RetrofitClient.apiService

    init {
        getAllUsers()
        getAllOrders()
    }

    fun getAllUsers() {
        viewModelScope.launch {
            try {
                val response = apiService.getAllUsers()
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                    Log.d("AdminViewModel", "Usuarios cargados: ${_users.value.size}")
                } else {
                    Log.e("AdminViewModel", "Error al cargar usuarios: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Excepción al cargar usuarios: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteUser(userId.toLong())
                if (response.isSuccessful) {
                    Log.d("AdminViewModel", "Usuario eliminado con éxito. Refrescando lista.")
                    getAllUsers() // Refrescar la lista de usuarios
                } else {
                    Log.e("AdminViewModel", "Error al eliminar usuario: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Excepción al eliminar usuario: ${e.message}")
            }
        }
    }

    fun getAllOrders() {
        viewModelScope.launch {
            try {
                val response = apiService.getAllOrders()
                if (response.isSuccessful) {
                    _orders.value = response.body() ?: emptyList()
                    Log.d("AdminViewModel", "Órdenes cargadas: ${_orders.value.size}")
                } else {
                    Log.e("AdminViewModel", "Error al cargar órdenes: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Excepción al cargar órdenes: ${e.message}")
            }
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            try {
                val response = apiService.updateOrderStatus(orderId, mapOf("estado" to status))
                if (response.isSuccessful) {
                    Log.d("AdminViewModel", "Estado de la orden actualizado. Refrescando lista.")
                    getAllOrders() // Refrescar la lista de órdenes
                } else {
                    Log.e("AdminViewModel", "Error al actualizar estado de la orden: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Excepción al actualizar estado de la orden: ${e.message}")
            }
        }
    }
}
