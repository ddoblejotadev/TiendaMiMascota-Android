package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.Usuario
import com.example.mimascota.model.UserWithOrders
import com.example.mimascota.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repo = AdminRepository()
    private val TAG = "AdminViewModel"

    private val _usersWithOrders = MutableStateFlow<List<UserWithOrders>>(emptyList())
    val usersWithOrders: StateFlow<List<UserWithOrders>> = _usersWithOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val usersResult = repo.getAllUsers()
            if (usersResult is AdminRepository.AdminResult.Error) {
                _error.value = "Error al cargar usuarios: ${usersResult.message}"
                _isLoading.value = false
                return@launch
            }

            val ordersResult = repo.getAllOrders()
            if (ordersResult is AdminRepository.AdminResult.Error) {
                _error.value = "Error al cargar órdenes: ${ordersResult.message}"
                _isLoading.value = false
                return@launch
            }

            val users = (usersResult as AdminRepository.AdminResult.Success).data
            val orders = (ordersResult as AdminRepository.AdminResult.Success).data

            val ordersByUser = orders.groupBy { it.usuarioId }
            val combinedData = users.map {
                UserWithOrders(it, ordersByUser[it.usuarioId.toLong()] ?: emptyList())
            }
            _usersWithOrders.value = combinedData
            _isLoading.value = false
        }
    }

    fun deleteUser(userId: Long, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repo.deleteUser(userId)) {
                is AdminRepository.AdminResult.Success -> {
                    onComplete(true, null)
                    refreshAll()
                }
                is AdminRepository.AdminResult.Error -> {
                    onComplete(false, result.message)
                }
            }
            _isLoading.value = false
        }
    }

    fun updateOrderStatus(orderId: Long, status: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repo.updateOrderStatus(orderId, status)) {
                is AdminRepository.AdminResult.Success -> {
                    onComplete(true, null)
                    refreshAll()
                }
                is AdminRepository.AdminResult.Error -> {
                    onComplete(false, result.message)
                }
            }
            _isLoading.value = false
        }
    }
}
