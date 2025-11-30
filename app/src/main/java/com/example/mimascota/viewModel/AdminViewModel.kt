package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.Usuario
import com.example.mimascota.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repo = AdminRepository()
    private val TAG = "AdminViewModel"

    private val _users = MutableStateFlow<List<Usuario>>(emptyList())
    val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private val _orders = MutableStateFlow<List<OrdenHistorial>>(emptyList())
    val orders: StateFlow<List<OrdenHistorial>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        fetchUsers()
        fetchOrders()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repo.getAllUsers()) {
                is AdminRepository.AdminResult.Success -> {
                    _users.value = result.data
                    _error.value = null
                }
                is AdminRepository.AdminResult.Error -> {
                    _error.value = result.message
                    Log.e(TAG, "fetchUsers error: ${result.message}")
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repo.getAllOrders()) {
                is AdminRepository.AdminResult.Success -> {
                    _orders.value = result.data
                    _error.value = null
                }
                is AdminRepository.AdminResult.Error -> {
                    _error.value = result.message
                    Log.e(TAG, "fetchOrders error: ${result.message}")
                }
            }
            _isLoading.value = false
        }
    }

    fun deleteUser(userId: Long, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repo.deleteUser(userId)) {
                is AdminRepository.AdminResult.Success -> {
                    onComplete(true, null)
                    fetchUsers()
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
                    fetchOrders()
                }
                is AdminRepository.AdminResult.Error -> {
                    onComplete(false, result.message)
                }
            }
            _isLoading.value = false
        }
    }

}

