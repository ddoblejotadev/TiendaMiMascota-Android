package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Producto
import com.example.mimascota.model.UserWithOrders
import com.example.mimascota.repository.AdminRepository
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val adminRepository = AdminRepository()
    private val productoRepository = ProductoRepository()

    private val _usersWithOrders = MutableStateFlow<List<UserWithOrders>>(emptyList())
    val usersWithOrders: StateFlow<List<UserWithOrders>> = _usersWithOrders.asStateFlow()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAdminData()
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val usersResult = adminRepository.getAllUsers()
                val ordersResult = adminRepository.getAllOrders()

                val users = when (usersResult) {
                    is AdminRepository.AdminResult.Success -> usersResult.data ?: emptyList()
                    is AdminRepository.AdminResult.Error -> {
                        _error.value = usersResult.message
                        Log.e("AdminViewModel", "Error al cargar usuarios: ${usersResult.message}")
                        emptyList()
                    }
                }

                val orders = when (ordersResult) {
                    is AdminRepository.AdminResult.Success -> ordersResult.data ?: emptyList()
                    is AdminRepository.AdminResult.Error -> {
                        _error.value = ordersResult.message
                        Log.e("AdminViewModel", "Error al cargar órdenes: ${ordersResult.message}")
                        emptyList()
                    }
                }

                val groupedData = users.map { user ->
                    val userOrders = orders.filter { it.usuarioId == user.usuarioId.toLong() }
                    UserWithOrders(user, userOrders)
                }

                _usersWithOrders.value = groupedData
                Log.d("AdminViewModel", "Datos de admin cargados. Usuarios: ${users.size}, Órdenes: ${orders.size}, Grupos con pedidos: ${groupedData.count { it.orders.isNotEmpty() }}")

                loadProductos()

            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
                Log.e("AdminViewModel", "Excepción al cargar datos de admin", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== PRODUCTOS CRUD ==========

    private fun loadProductos() {
        viewModelScope.launch {
            when (val result = productoRepository.getAllProductos()) {
                is ProductoRepository.ProductoResult.Success -> _productos.value = result.data
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun createProducto(producto: Producto) {
        viewModelScope.launch {
            when (val result = productoRepository.createProducto(producto)) {
                is ProductoRepository.ProductoResult.Success -> loadProductos() 
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun updateProducto(id: Int, producto: Producto) {
        viewModelScope.launch {
            when (val result = productoRepository.updateProducto(id, producto)) {
                is ProductoRepository.ProductoResult.Success -> loadProductos()
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun deleteProducto(id: Int) {
        viewModelScope.launch {
            when (val result = productoRepository.deleteProducto(id)) {
                is ProductoRepository.ProductoResult.Success -> loadProductos()
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    // ========== ÓRDENES ==========

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            when (val result = adminRepository.updateOrderStatus(orderId, status)) {
                is AdminRepository.AdminResult.Success -> {
                    Log.d("AdminViewModel", "Estado de la orden actualizado. Refrescando datos.")
                    loadAdminData()
                }
                is AdminRepository.AdminResult.Error -> {
                    _error.value = result.message
                }
            }
        }
    }

    // ========== USUARIOS ==========

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            when (val result = adminRepository.deleteUser(userId)) {
                is AdminRepository.AdminResult.Success -> {
                    Log.d("AdminViewModel", "Usuario eliminado. Refrescando datos.")
                    loadAdminData() // Recargar la lista de usuarios
                }
                is AdminRepository.AdminResult.Error -> {
                    _error.value = result.message
                    Log.e("AdminViewModel", "Error al eliminar usuario: ${result.message}")
                }
            }
        }
    }
}